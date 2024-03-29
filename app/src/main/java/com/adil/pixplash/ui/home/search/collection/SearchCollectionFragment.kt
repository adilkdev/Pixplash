package com.adil.pixplash.ui.home.search.collection

import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adil.pixplash.R
import com.adil.pixplash.ui.base.BaseFragment
import com.adil.pixplash.ui.home.search.CollectionQueryListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.error_layout.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.loadingView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchCollectionFragment: BaseFragment<SearchCollectionViewModel>(), CollectionQueryListener {

    private var query = ""

    @Inject
    lateinit var collectionAdapter: SearchCollectionAdapter

    /**
     * All type of listeners
     */

    private val reload: (value: Boolean) -> Unit = {
        //viewModel.onLoadMore(query = query)
        collectionAdapter.enableFooterRetry(false, "")
    }

    override fun provideLayoutId(): Int = R.layout.fragment_search

    override fun setupView(savedInstanceState: View) {
        loadingView.setAnimation("empty_state.json")
        rvSearch.adapter = collectionAdapter
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        rvSearch.apply {
            collectionAdapter.setTheReloadListener(reload)
            //setItemViewCacheSize(30)
            this.adapter = collectionAdapter
            val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            layoutManager = linearLayoutManager

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
//                    layoutManager?.run {
//                        if (this is LinearLayoutManager
//                            && itemCount > 0
//                            && itemCount == findLastVisibleItemPosition() + 1
//                        )
//                            //viewModel.onLoadMore(query = query)
//                    }
                }
            })

        }
    }

    override fun setupObservers() {
        super.setupObservers()

        viewModel.collections.observe(this, Observer {
            it.data?.run { collectionAdapter.appendList(this) }
            doneLoadingView()
            collectionAdapter.enableFooterRetry(false, null)
        })
        viewModel.error.observe(this, Observer {
            if (collectionAdapter.itemCount > 2) {
                collectionAdapter.enableFooterRetry(true, context?.resources?.getString(it))
            } else {
                onErrorView(it)
                cardRetry.setOnClickListener{
                    //viewModel.onLoadMore(query = query)
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
        errorLayout.visibility = View.VISIBLE
        rvSearch.visibility = View.GONE
        tvDescription.text = context?.resources?.getString(it)
        tvTitle.visibility = View.GONE
    }

    override fun onSearch(query: String) {
        viewModel.resetPage()
        if (query.isBlank()) {
            collectionAdapter.resetList()
            resetLoadingView()
        } else {
            loadingView()
            CoroutineScope(Dispatchers.IO).launch {
                collectionAdapter.resetList()
                this@SearchCollectionFragment.query = query
            }
            //viewModel.searchCollection(query = query)
        }
    }

    override fun onDestroy() {
        collectionAdapter.cancelAllJobs()
        super.onDestroy()
    }

}