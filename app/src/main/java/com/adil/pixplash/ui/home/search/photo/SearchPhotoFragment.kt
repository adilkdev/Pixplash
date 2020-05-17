package com.adil.pixplash.ui.home.search.photo

import android.util.TypedValue
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.adil.pixplash.R
import com.adil.pixplash.data.local.db.entity.Photo
import com.adil.pixplash.di.component.FragmentComponent
import com.adil.pixplash.ui.base.BaseFragment
import com.adil.pixplash.ui.home.search.PhotoQueryListener
import com.adil.pixplash.utils.AppConstants
import com.adil.pixplash.utils.view.GridSpacingItemDecoration
import kotlinx.android.synthetic.main.error_layout.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.loadingView
import javax.inject.Inject

class SearchPhotoFragment: BaseFragment<SearchPhotoViewModel>(), PhotoQueryListener {

    @Inject
    lateinit var searchPhotoAdapter: SearchPhotoAdapter

    private var query = ""

    /**
     * All type of listeners
     */
    private val savePhotos: (value: List<Photo>) -> Unit = {
        viewModel.savePhotos(it, AppConstants.PHOTO_TYPE_SEARCH)
    }

    private val removePhotos: (value: Boolean) -> Unit = {
        viewModel.removePhotos(AppConstants.PHOTO_TYPE_EXPLORE)
    }

    private val reload: (value: Boolean) -> Unit = {
        viewModel.onLoadMore(query = query)
        searchPhotoAdapter.enableFooterRetry(false, "")
    }

    override fun provideLayoutId(): Int = R.layout.fragment_search

    override fun setupView(savedInstanceState: View) {
        loadingView.setAnimation("empty_state.json")
        rvSearch.adapter = searchPhotoAdapter
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        rvSearch.apply {
            searchPhotoAdapter.setTheReloadListener(reload)
            searchPhotoAdapter.setSavePhotoInDBListener(savePhotos)
            searchPhotoAdapter.setRemovePhotoInDBListener(removePhotos)

            setItemViewCacheSize(30)
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
                        if (viewModel.loading.value==false) {
                            if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                                searchPhotoAdapter.enableFooter(true)
                                viewModel.onLoadMore(query = query)
                                //Log.e(TAG, "LOAD NEXT ITEM")
                            }
                        }
                    }
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    (recyclerView.layoutManager as StaggeredGridLayoutManager?)!!.invalidateSpanAssignments()
                    if(newState == RecyclerView.SCROLL_STATE_IDLE){
                        recyclerView.invalidateItemDecorations()
                    }
                }

            })
        }
    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.photos.observe(this, Observer {
            doneLoadingView()
            it.data?.run { searchPhotoAdapter.appendList(this, viewModel.getPage()) }
            searchPhotoAdapter.enableFooter(false)
            searchPhotoAdapter.enableFooterRetry(false, null)
        })
        viewModel.error.observe(this, Observer {
            if (searchPhotoAdapter.itemCount > 1) {
                searchPhotoAdapter.enableFooterRetry(true, context?.resources?.getString(it))
            } else {
                onErrorView(it)
                cardRetry.setOnClickListener{
                    viewModel.onLoadMore(query = query)
                    loadingView()
                }
            }
        })
    }

    private fun doneLoadingView() {
        loadingView.visibility = View.GONE
        errorLayout.visibility = View.GONE
        rvSearch.visibility = View.VISIBLE
    }

    private fun loadingView() {
        loadingView.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE
        rvSearch.visibility = View.GONE
    }

    private fun onErrorView(it: Int) {
        loadingView.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
        rvSearch.visibility = View.GONE
        tvDescription.text = context?.resources?.getString(it)
        tvTitle.visibility = View.GONE
    }

    override fun injectDependencies(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun onSearch(query: String) {
        if (query.isBlank()) {
            searchPhotoAdapter.resetList()
            loadingView.setAnimation("empty_state.json")
            loadingView()
        } else {
            searchPhotoAdapter.resetList()
            loadingView.setAnimation("loading.json")
            viewModel.searchPhotos(query = query)
            searchPhotoAdapter.setQuery(query)
            this.query = query
        }
    }

    override fun onDestroy() {
        viewModel.removePhotos(AppConstants.PHOTO_TYPE_SEARCH)
        searchPhotoAdapter.cancelAllJobs()
        super.onDestroy()
    }
}

