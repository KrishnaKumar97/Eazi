package com.nineleaps.eazipoc.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.nineleaps.eazipoc.models.GroupDatabaseModel
import com.nineleaps.eazipoc.repositories.GroupsRepository


class GroupViewModel : ViewModel() {
    private var groupsRepository = GroupsRepository()

    /**
     * Function calls the repository method to store the group in the database
     * @param groupDatabaseModel: GroupDatabaseModel object to be stored in the database
     */
    fun storeGroupInDB(groupDatabaseModel: GroupDatabaseModel) {
        groupsRepository.storeGroupInDB(groupDatabaseModel)
    }

    /**
     * Function calls the repository method to get the group list live data from the database
     * @param userName: username based on which the group list is fetched from the database
     */
    fun getGroupListLiveData(userName: String): LiveData<List<GroupDatabaseModel>> {
        return groupsRepository.getGroupListLiveData(userName)
    }

}