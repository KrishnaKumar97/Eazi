package com.nineleaps.eazipoc.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GroupModel(
    var groupName: String? = null

) : Parcelable