package com.nineleaps.eazipoc.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserModel(
    var jid: String? = null,
    var name: String? = null
):Parcelable