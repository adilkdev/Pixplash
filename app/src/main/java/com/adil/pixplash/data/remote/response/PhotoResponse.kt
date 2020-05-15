package com.adil.pixplash.data.remote.response

import com.google.gson.annotations.SerializedName

data class PhotoResponse (

    @SerializedName("id")
    val id: String,

    @SerializedName("width")
    val width: String,

    @SerializedName("height")
    val height: String,

    @SerializedName("color")
    val color: String,

    @SerializedName("urls")
    val urls: Urls,

    @SerializedName("user")
    val user: User
)