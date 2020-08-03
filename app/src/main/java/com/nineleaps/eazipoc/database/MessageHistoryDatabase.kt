package com.nineleaps.eazipoc.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nineleaps.eazipoc.models.MessageDatabaseModel

@Database(entities = [MessageDatabaseModel::class], version = 1)
abstract class MessageHistoryDatabase : RoomDatabase() {
    abstract val messageHistoryDAO: MessageHistoryDAO
}