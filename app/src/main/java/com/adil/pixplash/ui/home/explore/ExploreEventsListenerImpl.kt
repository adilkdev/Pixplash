package com.adil.pixplash.ui.home.explore

import com.adil.pixplash.data.local.db.entity.Photo
import com.adil.pixplash.utils.AppConstants

/**
 *  This class handles all the events in the explore fragment.
 */

class ExploreEventsListenerImpl(
    private val viewModel: ExploreViewModel,
    private val exploreAdapter: ExploreAdapter
) :
    ExploreEventsListener {

    override fun onSavePhotos(photos: List<Photo>) {
        viewModel.savePhotos(photos, AppConstants.PHOTO_TYPE_EXPLORE)
    }

    override fun onRemovePhotos() {
        viewModel.removePhotos(AppConstants.PHOTO_TYPE_EXPLORE)
    }

    override fun onOrderByStateChanged(orderBy: String) {
        viewModel.updateState(orderBy)
        exploreAdapter.resetList()
    }

    override fun onReload() {
        viewModel.onLoadMore()
        exploreAdapter.enableFooterRetry(false, "")
    }


}