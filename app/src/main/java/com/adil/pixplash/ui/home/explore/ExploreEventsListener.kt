package com.adil.pixplash.ui.home.explore

import com.adil.pixplash.data.local.db.entity.Photo

interface ExploreEventsListener {

    fun onSavePhotos(photos: List<Photo>)

    fun onRemovePhotos()

    fun onSortByStateChanged(sortBy: SortBy)

    fun onReload()

}