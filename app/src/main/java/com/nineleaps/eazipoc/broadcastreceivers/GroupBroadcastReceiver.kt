package com.nineleaps.eazipoc.broadcastreceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nineleaps.eazipoc.models.GroupModel
import com.nineleaps.eazipoc.services.GroupService

class GroupBroadcastReceiver : BroadcastReceiver() {
    var groupLiveData: MutableLiveData<ArrayList<GroupModel>> = MutableLiveData()

    fun getGroups(): LiveData<ArrayList<GroupModel>> {
        return groupLiveData
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            GroupService.GROUP_FETCH -> {
                groupLiveData.value = intent.getParcelableArrayListExtra("fetched_groups")
            }
        }
    }
}