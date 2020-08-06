package com.nineleaps.eazipoc.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.nineleaps.eazipoc.models.GroupDatabaseModel
import com.nineleaps.eazipoc.models.GroupModel
import com.nineleaps.eazipoc.models.MessageDatabaseModel
import com.nineleaps.eazipoc.repositories.GroupRepository
import com.nineleaps.eazipoc.repositories.GroupsRepository
import com.nineleaps.eazipoc.repositories.MessageHistoryRepository
import androidx.lifecycle.MutableLiveData


class GroupViewModel : ViewModel() {
    private var groupsRepository = GroupsRepository()

    fun storeGroupInDB(groupDatabaseModel: GroupDatabaseModel) {
        groupsRepository.storeGroupInDB(groupDatabaseModel)
    }

    fun getGroupListLiveData(userName: String): LiveData<List<GroupDatabaseModel>> {
        return groupsRepository.getGroupListLiveData(userName)
    }

    fun getData(): LiveData<ArrayList<GroupModel>>? {
        return GroupRepository.instance().data
    }
}