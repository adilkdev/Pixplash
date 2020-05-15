package com.adil.pixplash.data.remote.response

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.google.gson.annotations.SerializedName

data class User(
    @ColumnInfo(name = "username")
    @SerializedName("username")
    val username: String,

    @ColumnInfo(name = "name")
    @SerializedName("name")
    val name: String)

//data class UserLink (
//    @ColumnInfo(name = "user_link")
//    @SerializedName("html")
//    val html: String
//)