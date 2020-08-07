package com.nineleaps.eazipoc

import android.app.Application
import androidx.room.Room
import com.nineleaps.eazipoc.database.EaziDatabase
import org.jivesoftware.smack.tcp.XMPPTCPConnection

class ApplicationClass : Application() {

    companion object {
        lateinit var eaziDatabase: EaziDatabase
        lateinit var connection: XMPPTCPConnection
    }

    /**
     * Builds the database object using Room databaseBuilder
     */
    override fun onCreate() {
        super.onCreate()
        eaziDatabase = Room.databaseBuilder(
            applicationContext,
            EaziDatabase::class.java,
            "MessageHistoryDB"
        ).fallbackToDestructiveMigration().build()

    }
}