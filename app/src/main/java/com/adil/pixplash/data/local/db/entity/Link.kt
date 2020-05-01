package com.adil.pixplash.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import org.jetbrains.annotations.NotNull

data class Link(

    @ColumnInfo(name = "download")
    @SerializedName("download")
    val download: String

)