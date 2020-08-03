package com.nineleaps.eazipoc.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MessageHistory")
data class MessageDatabaseModel(
    @PrimaryKey
    var uniqueId: Int? = null,
    var groupName: String? = null,
    var userNickName: String? = null,
    var messageBody: String? = null
)