package de.luh.hci.mid.monumentgo.core.data.db

import android.content.Context
import android.util.Log
import de.luh.hci.mid.monumentgo.BuildConfig
import de.luh.hci.mid.monumentgo.core.data.model.UserProfile
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import io.ktor.http.HttpStatusCode.Companion.Created
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.serializer

sealed interface AuthResponse {
    data class Success(val profile: UserProfile) : AuthResponse
    data class Error(val message: String?) : AuthResponse
}

object DatabaseProvider {
    lateinit var supabase: SupabaseClient
        private set

    @OptIn(ExperimentalSerializationApi::class)
    fun initialize(context: Context) {
        supabase = createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            defaultSerializer = KotlinXSerializer(Json {
                namingStrategy = JsonNamingStrategy.SnakeCase
            })
            install(Auth)
            install(Postgrest)
        }
    }

    fun signUpNewUser(username: String, email: String, password: String): Flow<AuthResponse> =
        flow {
            try {
                val user = supabase.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                    this.data = buildJsonObject { put("username", username) }
                }
                if (user == null) {
                    emit(AuthResponse.Error("Disable auto-confirm!"))
                } else {
                    Log.d("auth", "Created user successfully. Retrieve profile:")
                    val result = supabase.postgrest.rpc (
                        "get_profile_with_level",
                        buildJsonObject {
                            put("user_id", user.id)
                        }
                    )
                    Log.d("auth", result.data)
                    val profile = result.decodeAs<UserProfile>()
                    emit(AuthResponse.Success(profile))
                }
            } catch (e: Exception) {
                emit(AuthResponse.Error(e.localizedMessage))
            }
        }

    fun signInWithEmail(email: String, password: String): Flow<AuthResponse> = flow {
        try {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val user = supabase.auth.currentUserOrNull()
            if (user == null) {
                emit(AuthResponse.Error("Failed retrieving user"))
            } else {
                val profile = supabase.postgrest.rpc (
                    "get_profile_with_level",
                    buildJsonObject {
                        put("user_id", user.id)
                    }
                ).decodeSingle<UserProfile>()
                emit(AuthResponse.Success(profile))
            }
        } catch (e: Exception) {
            emit(AuthResponse.Error(e.localizedMessage))
        }
    }
}