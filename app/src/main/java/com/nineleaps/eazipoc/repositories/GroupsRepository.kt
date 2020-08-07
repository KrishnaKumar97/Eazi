package com.nineleaps.eazipoc.repositories

import androidx.lifecycle.LiveData
import com.nineleaps.eazipoc.ApplicationClass
import com.nineleaps.eazipoc.models.GroupDatabaseModel

class GroupsRepository {
    /**
     * Function to call the method to insert a group into the database
     * @param groupDatabaseModel: Instance of GroupDatabaseModel which is to be inserted into the database
     */
    fun storeGroupInDB(groupDatabaseModel: GroupDatabaseModel) {
        ApplicationClass.eaziDatabase.eaziDAO.insertGroup(groupDatabaseModel)
    }

    /**
     * Function to call the method to fetch the group list from the database
     * @param userName: query the group list from the database based on the userName
     * @return: Livedata of list of GroupDatabaseModel
     */
    fun getGroupListLiveData(userName: String): LiveData<List<GroupDatabaseModel>> {
        return ApplicationClass.eaziDatabase.eaziDAO.fetchGroups(userName)
    }
}