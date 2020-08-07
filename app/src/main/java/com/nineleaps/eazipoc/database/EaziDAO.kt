package com.nineleaps.eazipoc.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.TypeConverters
import com.nineleaps.eazipoc.models.GroupDatabaseModel
import com.nineleaps.eazipoc.models.MessageDatabaseModel
import com.nineleaps.eazipoc.utils.TypeConverterObject

@Dao
interface EaziDAO {
    @Insert
    @TypeConverters(TypeConverterObject::class)
    fun insertMessageList(listOfMessages:List<MessageDatabaseModel>)

    @Insert
    fun insertMessage(messageDatabaseModel: MessageDatabaseModel)

    @Query("SELECT * FROM MessageHistory WHERE groupName=:group_name")
    @TypeConverters(TypeConverterObject::class)
    fun fetchAllMessages(group_name: String): LiveData<List<MessageDatabaseModel>>

    @Query("DELETE FROM MessageHistory WHERE groupName=:group_name")
    fun deleteMessages(group_name: String)

    @Insert
    fun insertGroup(groupDatabaseModel: GroupDatabaseModel)

    @Query("SELECT DISTINCT groupName,userNickName FROM Groups WHERE userNickName=:user_name")
    @TypeConverters(TypeConverterObject::class)
    fun fetchGroups(user_name: String): LiveData<List<GroupDatabaseModel>>
}