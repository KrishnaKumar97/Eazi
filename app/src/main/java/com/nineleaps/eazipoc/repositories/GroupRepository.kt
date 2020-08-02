package com.nineleaps.eazipoc.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.nineleaps.eazipoc.models.GroupModel

class GroupRepository private constructor() {

    private val mData = MediatorLiveData<ArrayList<GroupModel>>()

    val data: LiveData<ArrayList<GroupModel>>
        get() = mData

    fun addDataSource(data: LiveData<ArrayList<GroupModel>>) {
        mData.addSource<ArrayList<GroupModel>>(data) { mData.setValue(it) }
    }

    fun removeDataSource(data: LiveData<ArrayList<GroupModel>>) {
        mData.removeSource<ArrayList<GroupModel>>(data)
    }

    companion object {

        private val INSTANCE = GroupRepository()

        fun instance(): GroupRepository {
            return INSTANCE
        }
    }
}