package com.adil.pixplash.ui.home.collection.fragment

import android.content.Intent
import android.graphics.Outline
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewOutlineProvider
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.adil.pixplash.R
import com.adil.pixplash.data.local.prefs.UserPreferences
import com.adil.pixplash.di.component.FragmentComponent
import com.adil.pixplash.ui.base.BaseFragment
import com.adil.pixplash.ui.home.search.SearchActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.error_layout.*
import kotlinx.android.synthetic.main.fragment_explore.*
import kotlinx.android.synthetic.main.fragment_explore.loadingView
import javax.inject.Inject

class CollectionFragment: BaseFragment<CollectionViewModel>() {

    companion object {
        val TAG = "CollectionFragment"

        fun newInstance() : CollectionFragment {
            val args = Bundle()
            val fragment =
                CollectionFragment()
            fragment.arguments = args
            return fragment
        }

        val TYPE_ALL = 0
        val TYPE_FEATURED = 1
    }

    @Inject
    lateinit var prefs: UserPreferences

    @Inject
    lateinit var collectionAdapter: CollectionAdapter

    /**
     * All type of listeners
     */
    private val orderByClick: (Int) -> Unit = { type ->
        Log.e("Adil","$type")
        viewModel.updateType(type)
        collectionAdapter.resetList()
        viewModel.onLoadMore()
    }

    private val reload: (value: Boolean) -> Unit = {
        viewModel.onLoadMore()
        collectionAdapter.enableFooterRetry(false, "")
    }


    override fun provideLayoutId(): Int = R.layout.fragment_explore

    override fun setupView(savedInstanceState: View) {
        setupRecyclerView()

//        tvBigTitle.text = "Collections"
//
//        val viewOutlineProvider: ViewOutlineProvider = object : ViewOutlineProvider() {
//            override fun getOutline(view: View, outline: Outline) {
//                val cornerRadiusDP = 35f
//                val cornerRadius = TypedValue.applyDimension(
//                    TypedValue.COMPLEX_UNIT_DIP,
//                    cornerRadiusDP,
//                    resources.displayMetrics
//                )
//                outline.setRoundRect(0, - cornerRadius.toInt(), view.width, ((view.height) ), cornerRadius)
//            }
//        }
//        relative.outlineProvider = viewOutlineProvider
//        relative.clipToOutline = true
//
//
//        searchView.setOnClickListener {
//            val options: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                this.activity!!,
//                it,  // Starting view
//                "search_transition" // The String
//            )
//            context?.startActivity(Intent(this.activity, SearchActivity::class.java), options.toBundle())
//        }
    }

    private fun setupRecyclerView() {
        doneLoadingView()
        rvExplore.apply {

            setItemViewCacheSize(30)
            this.adapter = collectionAdapter
            val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            layoutManager = linearLayoutManager

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    layoutManager?.run {
                        if (this is LinearLayoutManager
                            && itemCount > 0
                            && itemCount == findLastVisibleItemPosition() + 1
                        )
                            viewModel.onLoadMore()
                    }
                }
            })

        }
    }

    override fun setupObservers() {
        super.setupObservers()

        collectionAdapter.setSortingListener(orderByClick)
        collectionAdapter.setTheReloadListener(reload)

        viewModel.randomPhoto.observe(this, Observer {
            //Picasso.get().load(it).placeholder(R.drawable.placeholder).into(ivBanner)
            Handler().postDelayed({
                collectionAdapter.setBannerImage(it)
                Log.e("Adil","called")
            }, 1000)
        })
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
        collectionAdapter.cancelAllJobs()
        super.onDestroy()
    }

}