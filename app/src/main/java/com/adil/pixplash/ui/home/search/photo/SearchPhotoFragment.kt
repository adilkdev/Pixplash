package com.adil.pixplash.ui.home.search.photo

import android.util.TypedValue
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.adil.pixplash.R
import com.adil.pixplash.data.local.db.entity.Photo
import com.adil.pixplash.ui.base.BaseFragment
import com.adil.pixplash.ui.home.search.PhotoQueryListener
import com.adil.pixplash.utils.AppConstants
import com.adil.pixplash.utils.view_helpers.GridSpacingItemDecoration
import kotlinx.android.synthetic.main.error_layout.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.loadingView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchPhotoFragment: BaseFragment<SearchPhotoViewModel>(), PhotoQueryListener, FragmentListeners {

    @Inject
    lateinit var searchPhotoAdapter: SearchPhotoAdapter

    private var query = ""

    override fun provideLayoutId(): Int = R.layout.fragment_search

    override fun setupView(savedInstanceState: View) {
        loadingView.setAnimation("empty_state.json")
        rvSearch.adapter = searchPhotoAdapter
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        rvSearch.apply {

            setHasFixedSize(true)
            setItemViewCacheSize(30)
            val gridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            //gridLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
            layoutManager = gridLayoutManager
            val itemSpacingDP = 14f
            val itemSpacing: Int = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                itemSpacingDP,
                resources.displayMetrics
            ).toInt()
            addItemDecoration(GridSpacingItemDecoration(itemSpacing, false))

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
                    if(newState == RecyclerView.SCROLL_STATE_IDLE){
                        recyclerView.invalidateItemDecorations()
                        //(recyclerView.layoutManager as StaggeredGridLayoutManager?)!!.invalidateSpanAssignments()
                    }
                }

            })
        }
    }

    override fun setupObservers() {
        super.setupObservers()
        searchPhotoAdapter.setListener(this)
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
        loadingViewLoading.visibility = View.GONE
        errorLayout.visibility = View.GONE
        rvSearch.visibility = View.VISIBLE
    }

    private fun loadingView() {
        loadingView.visibility = View.GONE
        loadingViewLoading.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE
        rvSearch.visibility = View.GONE
    }

    private fun resetLoadingView() {
        loadingView.visibility = View.VISIBLE
        loadingViewLoading.visibility = View.GONE
        errorLayout.visibility = View.GONE
        rvSearch.visibility = View.GONE
    }

    private fun onErrorView(it: Int) {
        loadingView.visibility = View.GONE
        loadingViewLoading.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
        rvSearch.visibility = View.GONE
        tvDescription.text = context?.resources?.getString(it)
        tvTitle.visibility = View.GONE
    }

    override fun onSearch(query: String) {
        viewModel.resetPage()
        if (query.isBlank()) {
            searchPhotoAdapter.resetList()
            resetLoadingView()
        } else {
            loadingView()
            CoroutineScope(Dispatchers.IO).launch {
                searchPhotoAdapter.resetList()
                searchPhotoAdapter.setQuery(query)
                this@SearchPhotoFragment.query = query
            }
            //viewModel.searchPhotos(query = query)
        }
    }

    override fun onDestroy() {
        viewModel.removePhotos(AppConstants.PHOTO_TYPE_SEARCH)
        searchPhotoAdapter.cancelAllJobs()
        super.onDestroy()
    }

    override fun onSave(value: List<Photo>) {
        viewModel.savePhotos(value, AppConstants.PHOTO_TYPE_SEARCH)
    }

    override fun onRemove(value: Boolean) {
        viewModel.removePhotos(AppConstants.PHOTO_TYPE_SEARCH)
    }

    override fun onReload(value: Boolean) {
        viewModel.onLoadMore(query = query)
        searchPhotoAdapter.enableFooterRetry(false, "")
    }
}

