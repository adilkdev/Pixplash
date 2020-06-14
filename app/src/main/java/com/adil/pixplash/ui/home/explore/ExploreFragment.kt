package com.adil.pixplash.ui.home.explore

import android.content.Intent
import android.graphics.Outline
import android.os.Bundle
import android.os.Handler
import android.transition.Fade
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewOutlineProvider
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.adil.pixplash.R
import com.adil.pixplash.data.local.db.entity.Photo
import com.adil.pixplash.data.local.prefs.UserPreferences
import com.adil.pixplash.di.component.FragmentComponent
import com.adil.pixplash.ui.base.BaseFragment
import com.adil.pixplash.ui.home.search.SearchActivity
import com.adil.pixplash.utils.AppConstants
import com.adil.pixplash.utils.view.GridSpacingItemDecoration
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.error_layout.*
import kotlinx.android.synthetic.main.fragment_explore.*
import kotlinx.android.synthetic.main.fragment_explore.loadingView
import javax.inject.Inject


class ExploreFragment: BaseFragment<ExploreViewModel>() {

    companion object {
        const val TAG = "ExploreFragment"

        fun newInstance() : ExploreFragment {
            val args = Bundle()
            val fragment = ExploreFragment()
            fragment.arguments = args
            return fragment
        }
    }

    @Inject
    lateinit var prefs: UserPreferences

    @Inject
    lateinit var exploreAdapter: ExploreAdapter

    /**
     * All type of listeners
     */
    private val savePhotos: (value: List<Photo>) -> Unit = {
        viewModel.savePhotos(it, AppConstants.PHOTO_TYPE_EXPLORE)
    }

    private val removePhotos: (value: Boolean) -> Unit = {
        viewModel.removePhotos(AppConstants.PHOTO_TYPE_EXPLORE)
    }

    private val orderByClick: (String) -> Unit = { type ->
        viewModel.updateState(type)
        exploreAdapter.resetList()
    }
    private val reload: (value: Boolean) -> Unit = {
        viewModel.onLoadMore()
        exploreAdapter.enableFooterRetry(false, "")
    }

    override fun provideLayoutId(): Int = R.layout.fragment_explore

    override fun setupView(savedInstanceState: View) {
        setTransition()
        setupRecyclerView()
    }

    private fun setTransition() {
        val fade = Fade()
        fade.excludeTarget(android.R.id.statusBarBackground, true)
        fade.excludeTarget(android.R.id.navigationBarBackground, true)
        activity?.window?.enterTransition = fade
        activity?.window?.exitTransition = fade
    }

    private fun setupRecyclerView() {
        exploreAdapter.setSortingListener(orderByClick)
        exploreAdapter.setTheReloadListener(reload)
        exploreAdapter.setSavePhotoInDBListener(savePhotos)
        exploreAdapter.setRemovePhotoInDBListener(removePhotos)

        rvExplore.apply {
            /**
             * The below piece of code will remove the default animator applied to the recycler view.
             */
            itemAnimator = null
            setItemViewCacheSize(30)
            this.adapter = exploreAdapter
            val gridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            gridLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
            layoutManager = gridLayoutManager
            val itemSpacingDP = 12f
            val itemSpacing: Int = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                itemSpacingDP,
                resources.displayMetrics
            ).toInt()
            addItemDecoration(GridSpacingItemDecoration(itemSpacing, true))

            var pastVisibleItems = 0
            var visibleItemCount: Int
            var totalItemCount: Int

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    layoutManager.run {
                        visibleItemCount = (layoutManager as StaggeredGridLayoutManager).childCount
                        totalItemCount = (layoutManager as StaggeredGridLayoutManager).itemCount
                        var firstVisibleItems: IntArray? = null
                        firstVisibleItems =
                            (layoutManager as StaggeredGridLayoutManager).findFirstVisibleItemPositions(firstVisibleItems)
                        if (firstVisibleItems != null && firstVisibleItems.isNotEmpty()) {
                            pastVisibleItems = firstVisibleItems[0]
                        }
                        if (!viewModel.loading.value!!) {
                            if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                                exploreAdapter.enableFooter(true)
                                viewModel.onLoadMore()
                                //Log.e(TAG, "LOAD NEXT ITEM")
                            }
                        }
                    }
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if(newState == RecyclerView.SCROLL_STATE_IDLE){
                        recyclerView.invalidateItemDecorations()
                        (recyclerView.layoutManager as StaggeredGridLayoutManager?)!!.invalidateSpanAssignments()
                    }
                }

            })
        }
    }

    override fun setupObservers() {
        super.setupObservers()
        removePhotos(true)
        viewModel.randomPhoto.observe(this, Observer {
            Handler().postDelayed({
                exploreAdapter.setBannerImage(it)
                Log.e("Adil","called")
            }, 1000)
        })

        viewModel.photos.observe(this, Observer {
            it.data?.run { exploreAdapter.appendList(this, viewModel.getPage()) }
            doneLoadingView()
            exploreAdapter.enableFooter(false)
            exploreAdapter.enableFooterRetry(false, null)
        })

        viewModel.error.observe(this, Observer {
            if (exploreAdapter.itemCount > 2) {
                exploreAdapter.enableFooterRetry(true, context?.resources?.getString(it))
            } else {
                onErrorView(it)
                cardRetry.setOnClickListener{
                    viewModel.onLoadMore()
                    loadingView()
                }
            }
        })
    }

    private fun doneLoadingView() {
        loadingView.visibility = View.GONE
        errorLayout.visibility = View.GONE
        rvExplore.visibility = View.VISIBLE
    }

    private fun loadingView() {
        loadingView.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE
        rvExplore.visibility = View.GONE
    }

    private fun onErrorView(it: Int) {
        loadingView.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
        rvExplore.visibility = View.GONE
        tvDescription.text = context?.resources?.getString(it)
    }

    fun scrollToTop() {
        rvExplore.smoothSnapToPosition(0)
    }

    private fun RecyclerView.smoothSnapToPosition(position: Int, snapMode: Int = LinearSmoothScroller.SNAP_TO_START) {
        val MILLISECONDS_PER_INCH = 10f
        val MAX_SCROLL_ON_FLING_DURATION = 300
        val smoothScroller = object : LinearSmoothScroller(this.context) {
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
                return MILLISECONDS_PER_INCH / (displayMetrics?.densityDpi ?: 1)
            }
            override fun calculateTimeForScrolling(dx: Int): Int =
                Math.min(MAX_SCROLL_ON_FLING_DURATION, super.calculateTimeForScrolling(dx))
            override fun getVerticalSnapPreference(): Int = snapMode
            override fun getHorizontalSnapPreference(): Int = snapMode
        }
        smoothScroller.targetPosition = position
        layoutManager?.startSmoothScroll(smoothScroller)
    }

    override fun injectDependencies(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun onDestroy() {
        viewModel.removePhotos(AppConstants.PHOTO_TYPE_EXPLORE)
        exploreAdapter.cancelAllJobs()
        super.onDestroy()
    }

}