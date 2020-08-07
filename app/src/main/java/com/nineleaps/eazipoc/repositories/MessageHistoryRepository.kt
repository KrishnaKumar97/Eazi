package com.nineleaps.eazipoc.repositories

import androidx.lifecycle.LiveData
import com.nineleaps.eazipoc.ApplicationClass
import com.nineleaps.eazipoc.models.MessageDatabaseModel

class MessageHistoryRepository {
    /**
     * Function to call the method to insert a message into the database
     * @param messageDatabaseModel: Instance of MessageDatabaseModel which is to be inserted into the database
     */
    fun storeDataInDB(messageDatabaseModel: MessageDatabaseModel) {
        ApplicationClass.eaziDatabase.eaziDAO.insertMessage(messageDatabaseModel)
    }

    /**
     * Function to call the method to insert a list of message into the database
     * @param groupName: Group name to which the message list should be inserted
     * @param listMessageDatabaseModel: list of MessageDatabaseModel which is to be inserted into the database
     */
    fun storeListInDB(groupName: String, listMessageDatabaseModel: List<MessageDatabaseModel>) {
        ApplicationClass.eaziDatabase.eaziDAO.deleteMessages(groupName)
        ApplicationClass.eaziDatabase.eaziDAO.insertMessageList(
            listMessageDatabaseModel
        )
    }

    /**
     * Function to call the method to fetch the message list from the database
     * @param groupName: query the message database based on the groupName
     * @return: Livedata of list of MessageDatabaseModel
     */
    fun getMessageHistoryLiveData(groupName: String): LiveData<List<MessageDatabaseModel>> {
        return ApplicationClass.eaziDatabase.eaziDAO.fetchAllMessages(groupName)
    }
}