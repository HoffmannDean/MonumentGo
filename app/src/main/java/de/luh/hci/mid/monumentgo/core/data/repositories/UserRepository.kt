package de.luh.hci.mid.monumentgo.core.data.repositories

import android.util.Log
import de.luh.hci.mid.monumentgo.core.data.db.DatabaseProvider.supabase
import de.luh.hci.mid.monumentgo.core.data.model.UserProfile
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

sealed interface AuthResponse {
    data class Success(val profile: UserProfile) : AuthResponse
    data class Error(val message: String?) : AuthResponse
}


fun AuthRestException.toUserMessage(): String =
    when (statusCode) {
        400, 401 ->
            "Incorrect email or password."

        403 ->
            "Your account does not have access."

        422 ->
            "Invalid email or password format."

        429 ->
            "Too many attempts. Please wait and try again."

        in 500..599 ->
            "Server unavailable. Try again later."

        else ->
            "Unexpected error. Please try again."
    }

class UserRepository(
    private val applicationScope: CoroutineScope
) {
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    init {
        applicationScope.launch {
            userProfile.collect {
                Log.d("auth", "User profile updated: $it")
            }
        }
    }

    val userLevel: Int
        get() = userProfile.value?.level ?: -1

    private suspend fun updateUserProfile() {
        Log.d("auth", "Updating user profile...")
        val userId = supabase.auth.currentUserOrNull()?.id
        if (userId == null) throw Exception("User is not logged in")
        val profile = supabase.postgrest.rpc("get_profiles_with_level") {
            filter {
                eq("id", userId)
            }
        }.decodeSingle<UserProfile>()
        _userProfile.value = profile
        Log.e("db", _userProfile.value.toString())
    }


    suspend fun trySilentSignIn(): AuthResponse {
        Log.e("auth", "User is attempting silent sign in.")
        val user = supabase.auth.currentUserOrNull()
        if (user == null) {
            return AuthResponse.Error("Not logged in")
        }
        updateUserProfile()
        return AuthResponse.Success(_userProfile.value!!)
    }

    suspend fun signUpNewUser(username: String, email: String, password: String): AuthResponse {
        Log.e("auth", "User is signing up.")
        try {
            val user = supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
                this.data = buildJsonObject { put("username", username) }
            }
            if (user == null) {
                return AuthResponse.Error("Disable auto-confirm!")
            }
            updateUserProfile()
            return AuthResponse.Success(_userProfile.value!!)
        } catch (e: AuthRestException) {
            return AuthResponse.Error(e.toUserMessage())
        } catch (e: Exception) {
            return AuthResponse.Error(e.toString())
        }
    }

    suspend fun signInWithEmail(email: String, password: String): AuthResponse {
        Log.e("auth", "User is signing in.")
        try {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            updateUserProfile()
            return AuthResponse.Success(_userProfile.value!!)
        } catch (e: AuthRestException) {
            Log.e("auth", e.toUserMessage())
            return AuthResponse.Error(e.toUserMessage())
        } catch (e: Exception) {
            Log.e("auth", e.message.toString())
            return AuthResponse.Error(e.toString())
        }
    }

    suspend fun signOut() {
        Log.e("auth", "User is logging out.")
        supabase.auth.signOut()
        _userProfile.value = null
    }


    @kotlin.uuid.ExperimentalUuidApi
    suspend fun addToUserScore(score: Int) { // works with DEFINER
        try {
            val storedScore : Int = _userProfile.value?.points ?: 0
            println("CURRENT STORED SCORE: $storedScore")
            println("UUID: " + _userProfile.value?.id.toString())
            val profile = supabase.postgrest.rpc(
                "set_user_score",
                buildJsonObject {
                    put("user_id", _userProfile.value?.id.toString())
                    put("score", score + storedScore)
                }
            )
            updateUserProfile()

            println("profile updated: $_userProfile")
        } catch (e: Exception) {
            println("error during updating profile: $e")
        }
    }

    suspend fun getLeaderboard(): List<UserProfile> {
        return supabase.postgrest.rpc("get_leaderboard").decodeList<UserProfile>()
    }
}