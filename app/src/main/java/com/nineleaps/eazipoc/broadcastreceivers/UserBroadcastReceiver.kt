package com.nineleaps.eazipoc.broadcastreceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nineleaps.eazipoc.services.UserService
import com.nineleaps.eazipoc.models.UserModel

class UserBroadcastReceiver : BroadcastReceiver() {

    private var userLiveData: MutableLiveData<ArrayList<UserModel>> = MutableLiveData()

    fun getUsers(): LiveData<ArrayList<UserModel>> {
        return userLiveData
    }

    /**
     * Function executed when users are fetched from the server
     * Broadcast is sent from UserService class on fetching the list of users
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            UserService.USERS_FETCHED -> {
                userLiveData.value = intent.getParcelableArrayListExtra("fetched_users")
            }
        }
    }
}