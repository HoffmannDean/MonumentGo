package de.luh.hci.mid.monumentgo.core.data.repositories

import de.luh.hci.mid.monumentgo.core.data.db.DatabaseProvider.supabase
import de.luh.hci.mid.monumentgo.core.data.model.UserProfile
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

sealed interface AuthResponse {
    data class Success(val profile: UserProfile) : AuthResponse
    data class Error(val message: String?) : AuthResponse
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
        } catch (e: Exception) {
            return AuthResponse.Error(e.localizedMessage)
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
            ).decodeSingle<UserProfile>()
            return AuthResponse.Success(profile)
        } catch (e: Exception) {
            return AuthResponse.Error(e.localizedMessage)
        }
    }
}