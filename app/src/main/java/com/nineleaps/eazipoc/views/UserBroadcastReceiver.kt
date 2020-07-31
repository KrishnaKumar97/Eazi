package com.nineleaps.eazipoc.views

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nineleaps.eazipoc.UserService
import com.nineleaps.eazipoc.models.UserModel

class UserBroadcastReceiver : BroadcastReceiver() {

    var userLiveData: MutableLiveData<ArrayList<UserModel>> = MutableLiveData()

    fun getUsers(): LiveData<ArrayList<UserModel>> {
        return userLiveData
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            UserService.USERS_FETCHED -> {
                userLiveData.value = intent.getParcelableArrayListExtra("fetched_users")
            }
        }
    }
}