package com.nineleaps.eazipoc.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Groups")
class GroupDatabaseModel(
    @PrimaryKey
    var uniqueId: Int? = null,
    var groupName: String? = null,
    var userNickName: String? = null
    )
