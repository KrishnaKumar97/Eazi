package com.nineleaps.eazipoc.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.nineleaps.eazipoc.models.GroupModel
import com.nineleaps.eazipoc.repositories.GroupRepository

class GroupViewModel : ViewModel() {
    fun getData(): LiveData<ArrayList<GroupModel>>? {
        return GroupRepository.instance().data
    }
}