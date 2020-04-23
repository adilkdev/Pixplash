package com.adil.pixplash.data.remote.response

import com.google.gson.annotations.SerializedName

data class Urls(
    @SerializedName("regular")
    val regular: String,

    @SerializedName("small")
    val small: String,

    @SerializedName("thumb")
    val thumb: String
)