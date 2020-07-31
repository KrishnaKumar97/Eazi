package com.nineleaps.eazipoc.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.nineleaps.eazipoc.models.UserModel


class UserRepository private constructor() {

    private val mData = MediatorLiveData<ArrayList<UserModel>>()

    val data: LiveData<ArrayList<UserModel>>
        get() = mData

    fun addDataSource(data: LiveData<ArrayList<UserModel>>) {
        mData.addSource<ArrayList<UserModel>>(data) { mData.setValue(it) }
    }

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