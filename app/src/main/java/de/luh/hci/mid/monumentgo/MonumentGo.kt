package de.luh.hci.mid.monumentgo

import android.app.Application
import android.util.Log
import de.luh.hci.mid.monumentgo.core.data.db.DatabaseProvider

class MonumentGo : Application() {
    override fun onCreate() {
        super.onCreate()

        Log.d("db", "Initializing database")
        DatabaseProvider.initialize(this)
    }
}