package com.adil.pixplash.ui.home.explore

import android.graphics.Outline
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewOutlineProvider
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.adil.pixplash.R
import com.adil.pixplash.di.component.FragmentComponent
import com.adil.pixplash.ui.base.BaseFragment
import com.adil.pixplash.utils.view.GridSpacingItemDecoration
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.error_layout.*
import kotlinx.android.synthetic.main.fragment_explore.*
import kotlinx.android.synthetic.main.fragment_explore.loadingView


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

    private lateinit var exploreAdapter: ExploreAdapter

    override fun provideLayoutId(): Int = R.layout.fragment_explore

    override fun setupView(savedInstanceState: View) {
        val viewOutlineProvider: ViewOutlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                val cornerRadiusDP = 35f
                val cornerRadius = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    cornerRadiusDP,
                    resources.displayMetrics
                )
                outline.setRoundRect(0, - cornerRadius.toInt(), view.width, ((view.height) ), cornerRadius)
            }
        }
        relative.outlineProvider = viewOutlineProvider
        relative.clipToOutline = true
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val orderByClick: (String) -> Unit = { type ->
            exploreAdapter.resetList()
            viewModel.updateState(type)
        }
        val reload: (value: Boolean) -> Unit = {
            viewModel.onLoadMore()
        }
        exploreAdapter =
            ExploreAdapter(activity!!, orderByClick, reload)
        exploreAdapter.setHasStableIds(true)

        rvExplore.apply {

            setItemViewCacheSize(30)
            this.adapter = exploreAdapter
            val gridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            gridLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
            layoutManager = gridLayoutManager
            val itemSpacingDP = 12f
            val itemSpacing: Int = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                itemSpacingDP,
                resources.displayMetrics
            ).toInt()
            addItemDecoration(GridSpacingItemDecoration(itemSpacing))

            var pastVisibleItems: Int = 0
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
                        if (viewModel.loading.value!! == null || !viewModel.loading.value!!) {
                            if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                                viewModel.onLoadMore()
                                //Log.e("tag", "LOAD NEXT ITEM")
                            }
                        }
                    }
                }
            })
        }
    }

    override fun setupObservers() {
        super.setupObservers()

        viewModel.randomPhoto.observe(this, Observer {
            Picasso.get().load(it).into(ivBanner)
        })

        viewModel.photos.observe(this, Observer {
            it.data?.run { exploreAdapter.appendList(this) }
            doneLoadingView()
            exploreAdapter.enableFooterRetry(false)
        })

        viewModel.error.observe(this, Observer {
            if (exploreAdapter.itemCount > 2) {
                exploreAdapter.enableFooterRetry(true)
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

    fun RecyclerView.smoothSnapToPosition(position: Int, snapMode: Int = LinearSmoothScroller.SNAP_TO_START) {
        val smoothScroller = object : LinearSmoothScroller(this.context) {
            override fun getVerticalSnapPreference(): Int = snapMode
            override fun getHorizontalSnapPreference(): Int = snapMode
        }
        smoothScroller.targetPosition = position
        layoutManager?.startSmoothScroll(smoothScroller)
    }

    override fun injectDependencies(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

}