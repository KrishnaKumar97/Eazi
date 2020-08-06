package com.nineleaps.eazipoc.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nineleaps.eazipoc.models.GroupDatabaseModel
import com.nineleaps.eazipoc.models.MessageDatabaseModel

@Database(entities = [MessageDatabaseModel::class, GroupDatabaseModel::class], version = 2)
abstract class MessageHistoryDatabase : RoomDatabase() {
    abstract val messageHistoryDAO: MessageHistoryDAO
}