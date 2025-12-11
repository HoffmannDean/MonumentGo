package de.luh.hci.mid.monumentgo.core.data.db

import android.content.Context
import de.luh.hci.mid.monumentgo.BuildConfig
import de.luh.hci.mid.monumentgo.core.data.model.UserProfile
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

sealed interface AuthResponse {
    data class Success(val profile: UserProfile) : AuthResponse
    data class Error(val message: String?) : AuthResponse
}

object DatabaseProvider {
    lateinit var supabase: SupabaseClient
        private set

    fun initialize(context: Context) {
        supabase = createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            install(Auth)
            install(Postgrest)
        }
    }

    fun signUpNewUser(username: String, email: String, password: String): Flow<AuthResponse> =
        flow {
            try {
                val result = supabase.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                    this.data = buildJsonObject { put("username", username) }
                }
                if (result == null) {
                    emit(AuthResponse.Error("Disable auto-confirm!"))
                } else {
                    val profile = supabase.from("profile_with_level").select {
                        filter {
                            UserProfile::id eq result.id
                        }
                    }.decodeSingle<UserProfile>()
                    emit(AuthResponse.Success(profile))
                }
            } catch (e: Exception) {
                emit(AuthResponse.Error(e.localizedMessage))
            }
        }

    fun signInWithEmail(email: String, password: String): Flow<AuthResponse> = flow {
        try {
            val result = supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val user = supabase.auth.currentUserOrNull()
            if (user == null) {
                emit(AuthResponse.Error("Failed retrieving user"))
            } else {
                val profile = supabase.from("profile_with_level").select {
                    filter {
                        UserProfile::id eq user.id
                    }
                }.decodeSingle<UserProfile>()
                emit(AuthResponse.Success(profile))
            }
        } catch (e: Exception) {
            emit(AuthResponse.Error(e.localizedMessage))
        }
    }
}