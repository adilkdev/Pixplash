package com.adil.pixplash.data.local.db.entity

import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName

data class Url(

    @ColumnInfo(name = "thumb")
    @SerializedName("thumb")
    val thumb: String,

    @ColumnInfo(name = "small")
    @SerializedName("small")
    val small: String,

    @ColumnInfo(name = "regular")
    @SerializedName("regular")
    val regular: String,

    @ColumnInfo(name = "full")
    @SerializedName("full")
    val full: String,

    @ColumnInfo(name = "raw")
    @SerializedName("raw")
    val raw: String

)