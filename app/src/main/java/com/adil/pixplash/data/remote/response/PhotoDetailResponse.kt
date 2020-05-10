package com.adil.pixplash.data.remote.response

import com.adil.pixplash.ui.home.image_detail.Exif
import com.google.gson.annotations.SerializedName

data class PhotoDetailResponse (
    @SerializedName("exif")
    val exif: Exif
)