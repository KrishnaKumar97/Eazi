package com.nineleaps.eazipoc

import android.app.Application
import androidx.room.Room
import com.nineleaps.eazipoc.database.MessageHistoryDatabase
import org.jivesoftware.smack.tcp.XMPPTCPConnection

class ApplicationClass : Application() {

    companion object {
        lateinit var messageHistoryDatabase: MessageHistoryDatabase
        lateinit var connection: XMPPTCPConnection
    }

    override fun onCreate() {
        super.onCreate()
        messageHistoryDatabase = Room.databaseBuilder(
            applicationContext,
            MessageHistoryDatabase::class.java,
            "MessageHistoryDB"
        ).fallbackToDestructiveMigration().build()

    }
}