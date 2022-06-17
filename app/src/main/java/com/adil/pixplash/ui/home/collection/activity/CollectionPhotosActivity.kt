package com.adil.pixplash.ui.home.collection.activity

import android.graphics.Outline
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewOutlineProvider
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.adil.pixplash.R
import com.adil.pixplash.data.local.db.entity.Photo
import com.adil.pixplash.ui.base.BaseActivity
import com.adil.pixplash.utils.view_utils.GridSpacingItemDecoration
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_collection_photos.*
import kotlinx.android.synthetic.main.activity_collection_photos.errorLayout
import kotlinx.android.synthetic.main.activity_collection_photos.tvBigTitle
import kotlinx.android.synthetic.main.error_layout.*
import javax.inject.Inject

@AndroidEntryPoint
class CollectionPhotosActivity: BaseActivity<CollectionPhotosViewModel>() {

    private var coverPhotoUrl: String = ""
    private var extras: Bundle? = null

    @Inject
    lateinit var collectionPhotoAdapter: CollectionPhotoAdapter

    private val reload: (value: Boolean) -> Unit = {
        //viewModel.onLoadMore()
        collectionPhotoAdapter.enableFooterRetry(false, "")
    }

    private val savePhotos: (value: List<Photo>) -> Unit = {
        viewModel.savePhotos(it)
    }

    override fun provideLayoutId(): Int = R.layout.activity_collection_photos

    override fun setupView(savedInstanceState: Bundle?) {
        extras = intent.extras
        coverPhotoUrl = extras?.getString("cover").toString()
        Picasso.get().load(coverPhotoUrl).into(ivCover)
        tvBigTitle.text = extras?.getString("title")
        viewModel.setCollectionId(extras?.getString("id")!!)
        collectionPhotoAdapter.setCollectionId(extras?.getString("id")!!)
        setupRoundedLayout()
        setupRecyclerView()
    }

    private fun setupRoundedLayout() {
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
    }

    private fun setupRecyclerView() {
        collectionPhotoAdapter.setTheReloadListener(reload)
        collectionPhotoAdapter.setSavePhotoInDBListener(savePhotos)

        rvPhotos.apply {

            setItemViewCacheSize(30)
            this.adapter = collectionPhotoAdapter
            val gridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            //gridLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
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
                                collectionPhotoAdapter.enableFooter(true)
                                //viewModel.onLoadMore()
                                //Log.e(TAG, "LOAD NEXT ITEM")
                            }
                        }
                    }
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        recyclerView.invalidateItemDecorations()
//                        (recyclerView.layoutManager as StaggeredGridLayoutManager?)!!.invalidateSpanAssignments()
                    }
                }

            })
        }
    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.photos.observe(this, Observer {
            it.data?.run { collectionPhotoAdapter.appendList(this, viewModel.getPage()) }
            doneLoadingView()
            collectionPhotoAdapter.enableFooterRetry(false, null)
            collectionPhotoAdapter.enableFooter(false)
        })

        viewModel.error.observe(this, Observer {
            if (collectionPhotoAdapter.itemCount > 2) {
                collectionPhotoAdapter.enableFooterRetry(true, resources?.getString(it))
            } else {
                onErrorView(it)
                cardRetry.setOnClickListener{
                    //viewModel.onLoadMore()
                    loadingView()
                }
            }
        })
    }

    private fun doneLoadingView() {
        loadingView.visibility = View.GONE
        errorLayout.visibility = View.GONE
        rvPhotos.visibility = View.VISIBLE
    }

    private fun loadingView() {
        loadingView.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE
        rvPhotos.visibility = View.GONE
    }

    private fun onErrorView(it: Int) {
        loadingView.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
        rvPhotos.visibility = View.GONE
        tvDescription.text = resources?.getString(it)
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
        viewModel.removePhotos()
        super.onDestroy()
    }

}