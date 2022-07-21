package com.adil.pixplash.ui.home.explore

import com.adil.pixplash.utils.AppConstants

enum class SortBy(val value: String) {
    LATEST(AppConstants.SORT_BY_LATEST),
    OLDEST(AppConstants.SORT_BY_OLDEST),
    POPULAR(AppConstants.SORT_BY_POPULAR)
}
