package de.luh.hci.mid.monumentgo.core.data.db

import android.content.Context
import android.util.Log
import de.luh.hci.mid.monumentgo.BuildConfig
import de.luh.hci.mid.monumentgo.core.data.model.UserProfile
import de.luh.hci.mid.monumentgo.core.data.repositories.UserRepository
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
}