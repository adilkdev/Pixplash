package com.adil.pixplash.ui.home.search.photo

import android.view.View
import com.adil.pixplash.R
import com.adil.pixplash.di.component.FragmentComponent
import com.adil.pixplash.ui.base.BaseFragment
import com.adil.pixplash.ui.home.HomeViewModel

class SearchPhotoFragment: BaseFragment<HomeViewModel>() {

    override fun provideLayoutId(): Int = R.layout.fragment_search

    override fun setupView(savedInstanceState: View) {

    }

    override fun injectDependencies(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)
}