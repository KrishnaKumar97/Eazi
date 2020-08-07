package com.nineleaps.eazipoc.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.nineleaps.eazipoc.models.UserModel
import com.nineleaps.eazipoc.repositories.UserRepository


class UserViewModel : ViewModel() {
    /**
     * Function calls the repository data to get the user list live data from the database
     */
    fun getData(): LiveData<ArrayList<UserModel>>? {
        return UserRepository.instance().data
    }
}