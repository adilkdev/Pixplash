package com.adil.pixplash.ui.home.explore

import android.graphics.Outline
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewOutlineProvider
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.adil.pixplash.R
import com.adil.pixplash.data.remote.Networking
import com.adil.pixplash.data.repository.PhotoRepository
import com.adil.pixplash.di.component.FragmentComponent
import com.adil.pixplash.ui.base.BaseFragment
import com.adil.pixplash.utils.view.GridSpacingItemDecoration
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_explore.*

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

    private lateinit var adapter: ExploreAdapter

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
        adapter =
            ExploreAdapter(activity!!.applicationContext)
        rvExplore.adapter = adapter
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        rvExplore.layoutManager = layoutManager
        rvExplore.addItemDecoration(GridSpacingItemDecoration(25))
    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.photos.observe(this, Observer {
            adapter.updateList(it)
        })
        //fetchPhotos()
    }

    override fun injectDependencies(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    private fun fetchPhotos() {
        val ns = Networking.create(cacheDir = activity?.application!!.cacheDir, cacheSize = 10 * 1024 * 1024)
        val photoRepository = PhotoRepository(ns)
        photoRepository.fetchPhotos()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.e(TAG, "${it.size}")
                    adapter.updateList(it)
                },
                {
                    Log.e(TAG, "${it.localizedMessage}")
                }
            )
    }

}