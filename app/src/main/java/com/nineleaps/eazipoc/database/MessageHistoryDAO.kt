package com.nineleaps.eazipoc.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.TypeConverters
import com.nineleaps.eazipoc.models.MessageDatabaseModel
import com.nineleaps.eazipoc.utils.TypeConverterObject

@Dao
interface MessageHistoryDAO {
    @Insert
    fun insertMessage(messageDatabaseModel: MessageDatabaseModel)

    @Query("SELECT * FROM MessageHistory WHERE groupName=:group_name")
    @TypeConverters(TypeConverterObject::class)
    fun fetchAllMessage(group_name: String): List<MessageDatabaseModel>

}