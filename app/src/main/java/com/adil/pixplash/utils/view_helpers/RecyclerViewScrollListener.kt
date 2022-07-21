package com.adil.pixplash.utils.view_helpers

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class RecyclerViewScrollListener(private val isVertical: Boolean) :
    RecyclerView.OnScrollListener() {

    companion object {
        val TAG = RecyclerViewListener::class.simpleName
    }

    /** flag to store the loading state of the recycler view */
    lateinit var loading: MutableLiveData<Boolean>

    var pastVisibleItems = 0
    var visibleItemCount = 0
    var totalItemCount = 0

    /** delta axis stores the change in axis which can be either x or y axis. */
    var dAxis: Int = 0

    private var recyclerViewListener: RecyclerViewListener? = null

    fun setupRecyclerViewScrollListener(recyclerViewListener: RecyclerViewListener) {
        this.recyclerViewListener = recyclerViewListener
    }

    fun setupLoading(loading: MutableLiveData<Boolean>) {
        this.loading = loading
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        dAxis = if (isVertical) dy else dx

        if (dAxis > 0) {
            recyclerView.layoutManager?.let { it as StaggeredGridLayoutManager }?.run {
                visibleItemCount = childCount
                totalItemCount = itemCount
                var firstVisibleItems: IntArray? = null
                firstVisibleItems = findFirstVisibleItemPositions(firstVisibleItems)
                if (firstVisibleItems != null && firstVisibleItems.isNotEmpty()) {
                    pastVisibleItems = firstVisibleItems[0]
                }
            }

            /** check for last item reached only if the recyclerview is not in the loading state. */
            if (loading.value == false) {
                if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                    loading.postValue(true)
                    // Do pagination.. i.e. fetch new data
                    Log.d(TAG, "Last Item reached!")
                    recyclerViewListener?.loadMoreData()
                }
            }
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        recyclerView.layoutManager?.let { it as StaggeredGridLayoutManager }?.run { invalidateSpanAssignments() }
    }

}

interface RecyclerViewListener {
    fun loadMoreData()
}