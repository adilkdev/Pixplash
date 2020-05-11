package com.adil.pixplash.data.remote.response

import com.google.gson.annotations.SerializedName

data class Collection (

    @SerializedName("id")
    val id: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("total_photos")
    val photosCount: Int,

    @SerializedName("cover_photo")
    val coverPhoto: CoverPhoto

)

data class CoverPhoto (
    @SerializedName("urls")
    val urls: Urls
)