package com.adil.pixplash.ui.home

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.adil.pixplash.R
import com.adil.pixplash.di.component.FragmentComponent
import com.adil.pixplash.ui.base.BaseFragment

class HomeFragment: BaseFragment<HomeViewModel>() {

    companion object {
        val TAG = "HomeFragment"

        fun newInstance() : HomeFragment {
            val args = Bundle()
            val fragment = HomeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun provideLayoutId(): Int = R.layout.activity_home

    override fun setupView(savedInstanceState: View) {

    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.data.observe(this, Observer {

        })
    }

    override fun injectDependencies(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)
}