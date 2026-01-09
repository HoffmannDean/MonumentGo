package de.luh.hci.mid.monumentgo.core.data.repositories

import de.luh.hci.mid.monumentgo.core.data.db.DatabaseProvider.supabase
import de.luh.hci.mid.monumentgo.core.data.model.UserProfile
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

class UserRepository {
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    suspend private fun getUserProfile(userId: String): UserProfile {
        val profile = supabase.postgrest.rpc(
            "get_profiles_with_level",
            buildJsonObject {
                //put("user_id", userId)
            }
        ).decodeAs<UserProfile>()
        _userProfile.value = profile
        println(profile)
        return profile
    }


    suspend fun trySilentSignIn(): AuthResponse {
        val session = supabase.auth.currentSessionOrNull()
        val user = supabase.auth.currentUserOrNull()
        if (session == null || user == null) {
            return AuthResponse.Error("Not logged in")
        }
        val profile = getUserProfile(user.id)
        return AuthResponse.Success(profile)
    }

    suspend fun signUpNewUser(username: String, email: String, password: String): AuthResponse {
        try {
            val user = supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
                this.data = buildJsonObject { put("username", username) }
            }
            if (user == null) {
                return AuthResponse.Error("Disable auto-confirm!")
            }
            val profile = getUserProfile(user.id)
            return AuthResponse.Success(profile)
        } catch (e: AuthRestException) {
            return AuthResponse.Error(e.toUserMessage())
        } catch (e: Exception) {
            return AuthResponse.Error(e.toString())
        }
    }

    suspend fun signInWithEmail(email: String, password: String): AuthResponse {
        try {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val user = supabase.auth.currentUserOrNull()
            if (user == null) {
                return AuthResponse.Error("Failed retrieving user")
            }
            val profile = getUserProfile(user.id)
            return AuthResponse.Success(profile)
        } catch (e: AuthRestException) {
            return AuthResponse.Error(e.toUserMessage())
        } catch (e: Exception) {
            return AuthResponse.Error(e.toString())
        }
    }

    suspend fun signOut() {
        supabase.auth.signOut()
        _userProfile.value = null
    }


    @kotlin.uuid.ExperimentalUuidApi
    suspend fun setUserScore(score: Int) {
        try {
            println("PROFILE: " + _userProfile.value)
            val profile = supabase.postgrest.rpc(
                "set_user_score",
                buildJsonObject {
                    put("user_id", userProfile.value?.id.toString())
                    put("score", score)
                }
            ).decodeAs<UserProfile>()

            _userProfile.value = profile
            println("profile updated: $profile")
        } catch (e: Exception) {
            println("error during updating profile: $e")
        }
    }

    fun getUserProfile() : MutableStateFlow<UserProfile?> {
        return _userProfile
    }
}