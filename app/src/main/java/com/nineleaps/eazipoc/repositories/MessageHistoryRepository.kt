package com.nineleaps.eazipoc.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nineleaps.eazipoc.ApplicationClass
import com.nineleaps.eazipoc.models.MessageDatabaseModel

class MessageHistoryRepository() {
    private var messageHistoryListMutableLiveData = MutableLiveData<List<MessageDatabaseModel>>()

    fun storeDataInDB(messageDatabaseModel: MessageDatabaseModel) {
        ApplicationClass.messageHistoryDatabase.messageHistoryDAO.insertMessage(messageDatabaseModel)
    }

    fun storeListInDB(groupName: String, listMessageDatabaseModel: List<MessageDatabaseModel>) {
        ApplicationClass.messageHistoryDatabase.messageHistoryDAO.deleteMessages(groupName)
        ApplicationClass.messageHistoryDatabase.messageHistoryDAO.insertListMessages(
            listMessageDatabaseModel
        )
    }

    fun getMessageHistoryLiveData(groupName: String): LiveData<List<MessageDatabaseModel>> {
        return ApplicationClass.messageHistoryDatabase.messageHistoryDAO.fetchAllMessage(groupName)
    }
}