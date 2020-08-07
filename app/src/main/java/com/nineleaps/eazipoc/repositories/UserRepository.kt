package com.nineleaps.eazipoc.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.nineleaps.eazipoc.models.UserModel


class UserRepository private constructor() {

    private val mData = MediatorLiveData<ArrayList<UserModel>>()

    val data: LiveData<ArrayList<UserModel>>
        get() = mData

    /**
     * Function to add the data to the MediatorLiveData<ArrayList<UserModel>>
     * @param data: Livedata of ArrayList of UserModel to be added to the MediatorLiveData<ArrayList<UserModel>>
     */
    fun addDataSource(data: LiveData<ArrayList<UserModel>>) {
        mData.addSource<ArrayList<UserModel>>(data) { mData.setValue(it) }
    }

    /**
     * Function to remove the data from the MediatorLiveData<ArrayList<UserModel>>
     * @param data: Livedata of ArrayList of UserModel to be removed from the MediatorLiveData<ArrayList<UserModel>>
     */
    fun removeDataSource(data: LiveData<ArrayList<UserModel>>) {
        mData.removeSource<ArrayList<UserModel>>(data)
    }

    companion object {

        private val INSTANCE = UserRepository()

        fun instance(): UserRepository {
            return INSTANCE
        }
    }
}