package com.nineleaps.eazipoc.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.nineleaps.eazipoc.models.UserModel
import com.nineleaps.eazipoc.repositories.UserRepository


class UserViewModel : ViewModel() {
    fun getData(): LiveData<ArrayList<UserModel>>? {
        return UserRepository.instance().data
    }
}