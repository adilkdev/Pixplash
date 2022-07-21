package com.adil.pixplash.utils.view_helpers

import android.content.Context
import android.util.AttributeSet
import android.util.Log

class DynamicHeightNetworkImageView : androidx.appcompat.widget.AppCompatImageView {
    private var mAspectRatio = 1.5f

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    fun setAspectRatio(aspectRatio: Float) {
        mAspectRatio = aspectRatio
        //requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val measuredWidth: Int = measuredWidth
        val measuredHeight: Int = (measuredWidth * mAspectRatio).toInt()
        setMeasuredDimension(measuredWidth, measuredHeight)
    }
}