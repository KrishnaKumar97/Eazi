package com.nineleaps.eazipoc.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.nineleaps.eazipoc.models.UserModel
import com.nineleaps.eazipoc.repositories.UserRepository


class UserViewModel : ViewModel() {
    fun getData(): LiveData<ArrayList<UserModel>>? {
        // for simplicity return data directly to view
        return UserRepository.instance().data
    }
}