package de.luh.hci.mid.monumentgo

import android.app.Application
import android.util.Log
import de.luh.hci.mid.monumentgo.core.data.db.DatabaseProvider
import de.luh.hci.mid.monumentgo.core.data.repositories.MonumentRepository
import de.luh.hci.mid.monumentgo.core.data.repositories.UserRepository

class MonumentGo : Application() {
    lateinit var userRepository: UserRepository
        private set

    lateinit var monumentRepository: MonumentRepository
        private set


    override fun onCreate() {
        super.onCreate()

        userRepository = UserRepository()
        monumentRepository = MonumentRepository()

        Log.d("db", "Initializing database")
        DatabaseProvider.initialize(this)
    }
}