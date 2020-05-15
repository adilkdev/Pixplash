package com.adil.pixplash.data.remote.response

import com.adil.pixplash.data.local.db.entity.Photo
import com.google.gson.annotations.SerializedName

data class SearchCollectionResponse (

    @SerializedName("results")
    val results: List<Collection>

)