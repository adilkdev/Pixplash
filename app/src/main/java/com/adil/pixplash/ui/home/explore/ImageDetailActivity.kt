package com.adil.pixplash.ui.home.explore

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import com.adil.pixplash.R
import com.adil.pixplash.di.component.ActivityComponent
import com.adil.pixplash.ui.base.BaseActivity
import com.adil.pixplash.ui.home.HomeViewModel
import kotlinx.android.synthetic.main.activity_image_detail.*


class ImageDetailActivity: BaseActivity<HomeViewModel>() {

    override fun provideLayoutId(): Int = R.layout.activity_image_detail

    private var isDismissable = true

    lateinit var imageDetailAdapter: ImageDetailAdapter

    override fun setupView(savedInstanceState: Bundle?) {

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        rvImages.apply {
            val dismissListener: (value: Boolean) -> Unit = { value ->
                isDismissable = value
            }
            imageDetailAdapter = ImageDetailAdapter(dismissListener)
            adapter = imageDetailAdapter
        }

    }

    override fun setupObservers() {
        super.setupObservers()
    }

    private var y1 = 0f
    private  var y2 = 0f
    private var x1 = 0f
    private  var x2 = 0f
    private val MIN_DISTANCE = 300

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        val interpolator = AccelerateInterpolator()
        var isDownMotionEnabled = true
        if (isDismissable) {
            when (event!!.action) {
                MotionEvent.ACTION_DOWN -> {
                    y1 = event!!.y
                    x1 = event!!.x
                }
                MotionEvent.ACTION_MOVE -> {
                    isDownMotionEnabled = kotlin.math.abs(event!!.x - x1) < 30
                    if (event!!.y > y1 && isDownMotionEnabled) {
                        y2 = event!!.y
                        rvImages.translationY = y2-y1
                        bg.alpha = interpolator.getInterpolation(1-(rvImages.y/rvImages.height*0.7f))
                    }
                }
                MotionEvent.ACTION_UP -> {
                    //y2 = event!!.y
                    val deltaY: Float = y2 - y1
                    //Log.e("adil", "deltaY = $y2 - $y1 = $deltaY")
                    if (y2 > y1)
                        if ((deltaY) > MIN_DISTANCE) {
                            val alphaAnimation: ObjectAnimator =
                                ObjectAnimator.ofFloat(bg, "alpha", 0f)
                            val transAnimation: ObjectAnimator =
                                ObjectAnimator.ofFloat(rvImages, "translationY", y2 - y1, rvImages.height.toFloat())
                            val animatorSet = AnimatorSet()
                            animatorSet.playTogether(alphaAnimation, transAnimation)
                            animatorSet.duration = 100
                            animatorSet.interpolator = AccelerateInterpolator()
                            animatorSet.start()
                            animatorSet.addListener(object : Animator.AnimatorListener {
                                override fun onAnimationRepeat(animation: Animator?) {
                                }

                                override fun onAnimationEnd(animation: Animator?) {
                                    finish()
                                }

                                override fun onAnimationCancel(animation: Animator?) {
                                }

                                override fun onAnimationStart(animation: Animator?) {
                                }

                            })
                        } else {
                            val alphaAnimation: ObjectAnimator =
                                ObjectAnimator.ofFloat(bg, "alpha", 1f)
                            val transAnimation: ObjectAnimator =
                                ObjectAnimator.ofFloat(rvImages, "translationY", y2 - y1, 0f)
                            val animatorSet = AnimatorSet()
                            animatorSet.playTogether(alphaAnimation, transAnimation)
                            animatorSet.duration = 300
                            animatorSet.interpolator = AccelerateDecelerateInterpolator()
                            animatorSet.start()
                        }
                    y2 = 0f
                    y1 = 0f
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun injectDependencies(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.nothing, R.anim.slide_down)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0,0)
    }

}