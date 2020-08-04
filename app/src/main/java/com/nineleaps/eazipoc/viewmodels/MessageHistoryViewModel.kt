package com.nineleaps.eazipoc.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.nineleaps.eazipoc.models.MessageDatabaseModel
import com.nineleaps.eazipoc.repositories.MessageHistoryRepository

class MessageHistoryViewModel : ViewModel() {
    private var messageHistoryRepository = MessageHistoryRepository()

    fun storeMessageInDB(messageDatabaseModel: MessageDatabaseModel) {
        messageHistoryRepository.storeDataInDB(messageDatabaseModel)
    }

    fun storeMessageListInDB(
        groupName: String,
        listMessageDatabaseModel: List<MessageDatabaseModel>
    ) {
        messageHistoryRepository.storeListInDB(groupName, listMessageDatabaseModel)
    }

    fun getMessageHistoryLiveData(groupName: String): LiveData<List<MessageDatabaseModel>> {
        return messageHistoryRepository.getMessageHistoryLiveData(groupName)
    }
}