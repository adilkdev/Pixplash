package com.adil.pixplash.utils.view_utils

import android.content.Context
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearSmoothScroller
import kotlin.math.min

class PixplashLinearSmoothScroller(context: Context, private val snapMode: Int): LinearSmoothScroller(context) {

    companion object {
        const val MILLISECONDS_PER_INCH = 10f
        const val MAX_SCROLL_ON_FLING_DURATION = 300
    }

    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
        return MILLISECONDS_PER_INCH / (displayMetrics?.densityDpi ?: 1)
    }

    override fun calculateTimeForScrolling(dx: Int): Int =
        min(MAX_SCROLL_ON_FLING_DURATION, super.calculateTimeForScrolling(dx))

    override fun getHorizontalSnapPreference(): Int = snapMode
    override fun getVerticalSnapPreference(): Int = snapMode

}