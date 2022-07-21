package com.adil.pixplash.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.adil.pixplash.data.remote.response.User
import com.adil.pixplash.utils.AppConstants
import com.adil.pixplash.utils.extensions.emptyString
import com.google.gson.annotations.SerializedName
import org.jetbrains.annotations.NotNull

@Entity(tableName = "photo_entity")
data class Photo(

    @PrimaryKey(autoGenerate = true)
    @SerializedName("main_id")
    @NotNull
    val id: Long,

    @ColumnInfo(name = "photo_id")
    @SerializedName("id")
    val photoId: String,

    @ColumnInfo(name = "width")
    @SerializedName("width")
    val width: String,

    @ColumnInfo(name = "height")
    @SerializedName("height")
    val height: String,

    @ColumnInfo(name = "color")
    @SerializedName("color")
    val color: String,

    @Embedded
    @SerializedName("urls")
    val urls: Url,

    @Embedded
    @SerializedName("links")
    val download: Link,

    @ColumnInfo(name = "likes")
    @SerializedName("likes")
    val likes: String,

    @ColumnInfo(name = "photoType", defaultValue = "explore")
    var photoType: String,

    @Embedded
    @SerializedName("user")
    val user: User
) {

    companion object {
        fun getBlankPhotoItem() =
            Photo(
                0,
                emptyString(),
                emptyString(),
                emptyString(),
                emptyString(),
                Url(
                    emptyString(),
                    emptyString(),
                    emptyString(),
                    emptyString(),
                    emptyString()
                ),
                Link(emptyString()),
                emptyString(),
                AppConstants.PHOTO_TYPE_EXPLORE,
                User(emptyString(), emptyString())
            )
    }

}