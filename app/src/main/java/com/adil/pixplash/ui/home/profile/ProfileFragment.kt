package com.adil.pixplash.ui.home.profile

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.adil.pixplash.R
import com.adil.pixplash.di.component.FragmentComponent
import com.adil.pixplash.ui.base.BaseFragment
import com.adil.pixplash.ui.home.HomeViewModel

class ProfileFragment: BaseFragment<HomeViewModel>() {

    companion object {
        val TAG = "HomeFragment"

        fun newInstance() : ProfileFragment {
            val args = Bundle()
            val fragment = ProfileFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun provideLayoutId(): Int = R.layout.fragment_explore

    override fun setupView(savedInstanceState: View) {

    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.data.observe(this, Observer {

        })
    }

    override fun injectDependencies(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)
}