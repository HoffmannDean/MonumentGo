package de.luh.hci.mid.monumentgo.core.data.repositories

import de.luh.hci.mid.monumentgo.core.data.db.DatabaseProvider.supabase
import de.luh.hci.mid.monumentgo.core.data.model.UserProfile
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
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
            val profile = supabase.postgrest.rpc(
                "get_profile_with_level",
                buildJsonObject {
                    put("user_id", user.id)
                }
            ).decodeAs<UserProfile>()
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
            val profile = supabase.postgrest.rpc (
                "get_profile_with_level",
                buildJsonObject {
                    put("user_id", user.id)
                }
            ).decodeAs<UserProfile>()
            return AuthResponse.Success(profile)
        } catch (e: AuthRestException) {
            return AuthResponse.Error(e.toUserMessage())
        } catch (e: Exception) {
            return AuthResponse.Error(e.toString())
        }
    }
}