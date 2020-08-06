package com.nineleaps.eazipoc.repositories

import androidx.lifecycle.LiveData
import com.nineleaps.eazipoc.ApplicationClass
import com.nineleaps.eazipoc.models.GroupDatabaseModel

class GroupsRepository {
    fun storeGroupInDB(groupDatabaseModel: GroupDatabaseModel) {
        ApplicationClass.messageHistoryDatabase.messageHistoryDAO.insertGroup(groupDatabaseModel)
    }

    fun getGroupListLiveData(userName: String): LiveData<List<GroupDatabaseModel>> {
        return ApplicationClass.messageHistoryDatabase.messageHistoryDAO.fetchGroups(userName)
    }
}