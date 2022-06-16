package com.adil.pixplash.ui.home.explore

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.adil.pixplash.R
import com.adil.pixplash.ui.base.BaseFragment
import com.adil.pixplash.utils.AppConstants
import com.adil.pixplash.utils.AppConstants.MAX_SCROLL_ON_FLING_DURATION
import com.adil.pixplash.utils.AppConstants.MILLISECONDS_PER_INCH
import com.adil.pixplash.utils.view.GridSpacingItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.error_layout.*
import kotlinx.android.synthetic.main.fragment_explore.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.min

@AndroidEntryPoint
class ExploreFragment : BaseFragment<ExploreViewModel>() {

    companion object {
        const val TAG = "ExploreFragment"
        const val ITEM_SPACING_IN_DP = 14f

        fun newInstance(): ExploreFragment {
            val args = Bundle()
            val fragment = ExploreFragment()
            fragment.arguments = args
            return fragment
        }
    }

    @Inject
    lateinit var exploreAdapter: ExploreAdapter

    @Inject
    lateinit var exploreEventsListenerImpl: ExploreEventsListener

    override fun provideLayoutId(): Int = R.layout.fragment_explore

    override fun setupView(savedInstanceState: View) {
        CoroutineScope(Dispatchers.Default).launch {
            setupRecyclerView()
        }
    }

    private fun setupRecyclerView() {
        exploreAdapter.setTheExploreEventsListener(exploreEventsListenerImpl)

        rvExplore.apply {
            /** The below piece of code will remove the default animator applied to the recycler view. */
            itemAnimator = null
            setItemViewCacheSize(30)
            setHasFixedSize(true)
            adapter = exploreAdapter
            val gridLayoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            layoutManager = gridLayoutManager

            /** Adding padding around the single item view of recyclerview */
            val itemSpacing: Int = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                ITEM_SPACING_IN_DP,
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
                            (layoutManager as StaggeredGridLayoutManager).findFirstVisibleItemPositions(
                                firstVisibleItems
                            )
                        if (firstVisibleItems != null && firstVisibleItems.isNotEmpty()) {
                            pastVisibleItems = firstVisibleItems[0]
                        }
                        if (!viewModel.loading.value!!) {
                            if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                                exploreAdapter.enableFooter(true)
                                viewModel.onLoadMore()
                                Timber.tag(TAG).e("LOAD NEXT ITEM")
                            }
                        }
                    }
                }
            })
        }
    }

    override fun setupObservers() {
        super.setupObservers()
        exploreEventsListenerImpl.onRemovePhotos()
        viewModel.randomPhoto.observe(this) {
            exploreAdapter.setBannerImage(it)
        }

        viewModel.photos.observe(this) {
            it.data?.run { exploreAdapter.appendList(this, viewModel.getPage()) }
            doneLoadingView()
            exploreAdapter.enableFooter(false)
            exploreAdapter.enableFooterRetry(false, null)
        }

        viewModel.error.observe(this) {
            if (exploreAdapter.itemCount > 2) {
                exploreAdapter.enableFooterRetry(true, context?.resources?.getString(it))
            } else {
                onErrorView(it)
                cardRetry.setOnClickListener {
                    viewModel.onLoadMore()
                    loadingView()
                }
            }
        }
        viewModel.onLoadMore()
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

    private fun RecyclerView.smoothSnapToPosition(
        position: Int,
        snapMode: Int = LinearSmoothScroller.SNAP_TO_START
    ) {

        val smoothScroller = object : LinearSmoothScroller(this.context) {
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
                return MILLISECONDS_PER_INCH / (displayMetrics?.densityDpi ?: 1)
            }

            override fun calculateTimeForScrolling(dx: Int): Int =
                min(MAX_SCROLL_ON_FLING_DURATION, super.calculateTimeForScrolling(dx))

            override fun getHorizontalSnapPreference(): Int = snapMode
            override fun getVerticalSnapPreference(): Int = snapMode
        }
        smoothScroller.targetPosition = position
        layoutManager?.startSmoothScroll(smoothScroller)
    }

    override fun onDestroy() {
        viewModel.removePhotos(AppConstants.PHOTO_TYPE_EXPLORE)
        exploreAdapter.cancelAllJobs()
        super.onDestroy()
    }

}