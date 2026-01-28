package de.luh.hci.mid.monumentgo

import android.app.Application
import android.util.Log
import de.luh.hci.mid.monumentgo.core.data.db.DatabaseProvider
import de.luh.hci.mid.monumentgo.core.data.repositories.MonumentRepository
import de.luh.hci.mid.monumentgo.core.data.repositories.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class MonumentGo : Application() {
    lateinit var userRepository: UserRepository
        private set

    lateinit var monumentRepository: MonumentRepository
        private set


    override fun onCreate() {
        super.onCreate()

        val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        userRepository = UserRepository(applicationScope)
        monumentRepository = MonumentRepository()

        Log.d("db", "Initializing database")
        DatabaseProvider.initialize(this)
    }
}