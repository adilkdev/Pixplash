package com.adil.pixplash.ui.home.image_detail

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.adil.pixplash.R
import com.adil.pixplash.data.repository.PhotoRepository
import com.adil.pixplash.di.component.ActivityComponent
import com.adil.pixplash.ui.base.BaseActivity
import com.adil.pixplash.utils.AppConstants
import kotlinx.android.synthetic.main.activity_image_detail.*
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.reflect.typeOf


class ImageDetailActivity: BaseActivity<ImageDetailViewModel>() {

    private var isDismissible = true

    @Inject
    lateinit var imageDetailAdapter: ImageDetailAdapter

    @Inject
    lateinit var photoRepository: PhotoRepository

    @Inject
    lateinit var job: CompletableJob

    private var photoId: String = ""

    private var itemCount: Int = 0

    private var page = 0

    private var activeOrder = ""

    private var pagerType = ""

    private var collectionId = ""

    private var query = ""

    private var extras: Bundle? = null

    private val dismissListener: (value: Boolean) -> Unit = { value ->
        isDismissible = value
    }

    override fun provideLayoutId(): Int = R.layout.activity_image_detail

    override fun setupView(savedInstanceState: Bundle?) {

        extras = intent.extras
        photoId = extras?.get(AppConstants.ADAPTER_POSITION_PHOTO_ID) as String
        page = extras?.get(AppConstants.LOADED_PAGES) as Int
        viewModel.setPage(page)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        pagerType = extras?.getString("type")!!
        if (pagerType == AppConstants.PHOTO_TYPE_EXPLORE) {
            activeOrder = extras?.get(AppConstants.ACTIVE_ORDER) as String
            viewModel.setActiveOrder(activeOrder)
        } else if (pagerType == AppConstants.PHOTO_TYPE_COLLECTION) {
            collectionId = extras?.getString("collectionId")!!
        } else {
            query = extras?.getString("query")!!
        }
        setPagerType(pagerType, collectionId)

        imageDetailAdapter.setTheDismissListener(dismissListener)

        setupViewPager()

    }

    private fun setupViewPager() {
        rvImages.apply {
            adapter = imageDetailAdapter

            CoroutineScope(Dispatchers.IO + job).launch {
                setupData()
                withContext(Dispatchers.Main) {
                    post {
                        setCurrentItem(findPic(photoId), true)
                    }
                }
            }

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if (position == imageDetailAdapter.itemCount - 3) {
                        if (pagerType == AppConstants.PHOTO_TYPE_EXPLORE) {
                            viewModel.loadMore()
                        } else if (pagerType == AppConstants.PHOTO_TYPE_COLLECTION) {
                            viewModel.loadMoreCollectionPhotos()
                        } else {
                            viewModel.searchPhotos(query = query)
                        }
                    }
                }
            })
        }
    }

    private fun setupData() {
        CoroutineScope(Dispatchers.IO + job).launch {
            val photos = photoRepository.getAllPhotosFromDB(pagerType)
            imageDetailAdapter.appendList(photos)
            itemCount = photos.size
            page = itemCount / PhotoRepository.pageSize
        }
    }

    private fun setPagerType(type: String, id: String) {
        viewModel.setPagerType(type, id)
    }

    private fun findPic(id: String): Int {
        val tempList = photoRepository.getAllPhotosFromDB(pagerType)
        val item = tempList.find { it.photoId == id }
        return tempList.indexOf(item)
    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.photos.observe(this, Observer {
            it.data?.let { it1 -> imageDetailAdapter.appendList(it1) }
            Log.e("adil","$page")
        })
    }

    private var y1 = 0f
    private  var y2 = 0f
    private var x1 = 0f
    //private  var x2 = 0f
    private val MIN_DISTANCE = 250

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        val interpolator = AccelerateInterpolator()
        val isDownMotionEnabled: Boolean
        if (isDismissible) {
            when (event!!.action) {
                MotionEvent.ACTION_DOWN -> {
                    y1 = event.y
                    x1 = event.x
                }
                MotionEvent.ACTION_MOVE -> {
                    isDownMotionEnabled = kotlin.math.abs(event.x - x1) < 30
                    if (event.y > y1 && isDownMotionEnabled) {
                        y2 = event.y
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

    override fun onDestroy() {
        job.cancel()
        imageDetailAdapter.cancelAllJobs()
        super.onDestroy()
    }

}