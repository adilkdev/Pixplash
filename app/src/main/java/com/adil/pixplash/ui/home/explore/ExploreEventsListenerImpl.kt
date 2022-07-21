package com.adil.pixplash.ui.home.explore

import com.adil.pixplash.data.local.db.entity.Photo
import com.adil.pixplash.utils.AppConstants

/**
 *  This class handles all the events in the explore fragment.
 */

class ExploreEventsListenerImpl(
    private val exploreFragment: ExploreFragment
) :
    ExploreEventsListener {

    override fun onSavePhotos(photos: List<Photo>) {
        exploreFragment.viewModel.savePhotos(photos, AppConstants.PHOTO_TYPE_EXPLORE)
    }

    override fun onRemovePhotos() {
        exploreFragment.viewModel.removePhotos(AppConstants.PHOTO_TYPE_EXPLORE)
    }

    override fun onSortByStateChanged(sortBy: SortBy) {
        exploreFragment.viewModel.updateState(sortBy)
        exploreFragment.exploreAdapter.resetList()
        exploreFragment.viewModel.removePhotos(AppConstants.PHOTO_TYPE_EXPLORE)
        exploreFragment.viewModel.onLoadMore()
    }

    override fun onReload() {
        exploreFragment.viewModel.onLoadMore()
        exploreFragment.exploreAdapter.enableFooterRetry(false, "")
    }


}