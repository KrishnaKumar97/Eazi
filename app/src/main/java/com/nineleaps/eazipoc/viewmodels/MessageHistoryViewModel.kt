package com.nineleaps.eazipoc.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.nineleaps.eazipoc.models.MessageDatabaseModel
import com.nineleaps.eazipoc.repositories.MessageHistoryRepository

class MessageHistoryViewModel : ViewModel() {
    private var messageHistoryRepository = MessageHistoryRepository()

    /**
     * Function calls the repository method to store the message in the database
     * @param messageDatabaseModel: MessageDatabaseModel object to be stored in the database
     */
    fun storeMessageInDB(messageDatabaseModel: MessageDatabaseModel) {
        messageHistoryRepository.storeDataInDB(messageDatabaseModel)
    }

    /**
     * Function calls the repository method to store the list of message in the database
     * @param groupName: groupName under which the messages should be stored
     * @param listMessageDatabaseModel: List of MessageDatabaseModel objects to be stored in the database
     */
    fun storeMessageListInDB(
        groupName: String,
        listMessageDatabaseModel: List<MessageDatabaseModel>
    ) {
        messageHistoryRepository.storeListInDB(groupName, listMessageDatabaseModel)
    }

    /**
     * Function calls the repository method to get the message list live data from the database
     * @param groupName: groupName based on which the message list is fetched from the database
     */
    fun getMessageHistoryLiveData(groupName: String): LiveData<List<MessageDatabaseModel>> {
        return messageHistoryRepository.getMessageHistoryLiveData(groupName)
    }
}