package com.adil.pixplash.ui.home.explore

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.adil.pixplash.R
import com.adil.pixplash.ui.base.BaseFragment
import com.adil.pixplash.utils.AppConstants
import com.adil.pixplash.utils.view_helpers.GridSpacingItemDecoration
import com.adil.pixplash.utils.view_helpers.PixplashLinearSmoothScroller
import com.adil.pixplash.utils.view_helpers.RecyclerViewListener
import com.adil.pixplash.utils.view_helpers.RecyclerViewScrollListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.error_layout.*
import kotlinx.android.synthetic.main.fragment_explore.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ExploreFragment : BaseFragment<ExploreViewModel>(), RecyclerViewListener {

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

    override fun provideLayoutId(): Int = R.layout.fragment_explore

    @Inject
    lateinit var recyclerViewScrollListener: RecyclerViewScrollListener

    override fun setupView(savedInstanceState: View) {
        CoroutineScope(Dispatchers.Main).launch {
            setupRecyclerView()
        }
        cardRetry.setOnClickListener {
            viewModel.onLoadMore()
            showLoadingView()
        }
    }

    private fun setupRecyclerView() {
        exploreAdapter.setTheExploreEventsListener(ExploreEventsListenerImpl(this))

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

            recyclerViewScrollListener.setupRecyclerViewScrollListener(this@ExploreFragment)
            recyclerViewScrollListener.setupLoading(viewModel.loading)

            addOnScrollListener(recyclerViewScrollListener)
        }
    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.randomPhoto.observe(this) {
            exploreAdapter.setBannerImage(it)
        }

        viewModel.photos.observe(this) {
            it.data?.run { exploreAdapter.appendList(this, viewModel.getPage()) }
            hideLoadingView()
            exploreAdapter.enableFooter(false)
            exploreAdapter.enableFooterRetry(false, null)
        }

        viewModel.error.observe(this) {
            if (exploreAdapter.itemCount > 2) {
                exploreAdapter.enableFooterRetry(true, context?.resources?.getString(it))
            } else {
                onErrorView(it)
            }
        }

        /** triggering initial load when the app launches for the very first time. */
        viewModel.onLoadMore()
    }

    private fun hideLoadingView() {
        loadingView.visibility = View.GONE
        errorLayout.visibility = View.GONE
        rvExplore.visibility = View.VISIBLE
    }

    private fun showLoadingView() {
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

    /** this feature is used to smoothly scroll to a particular item */
    private fun RecyclerView.smoothSnapToPosition(
        position: Int,
        snapMode: Int = LinearSmoothScroller.SNAP_TO_START
    ) {
        val smoothScroller = this@ExploreFragment.context?.run {
            PixplashLinearSmoothScroller(this, snapMode)
        }?.also { scroller ->
            scroller.targetPosition = position
        }
        smoothScroller?.targetPosition = position
        layoutManager?.startSmoothScroll(smoothScroller)
    }

    override fun loadMoreData() {
        exploreAdapter.enableFooter(true)
        viewModel.onLoadMore()
    }

    override fun onDestroy() {
        viewModel.removePhotos(AppConstants.PHOTO_TYPE_EXPLORE)
        exploreAdapter.cancelAllJobs()
        super.onDestroy()
    }

}