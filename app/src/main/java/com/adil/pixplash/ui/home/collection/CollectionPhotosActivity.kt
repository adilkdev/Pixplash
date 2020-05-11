package com.adil.pixplash.ui.home.collection

import android.os.Bundle
import com.adil.pixplash.R
import com.adil.pixplash.di.component.ActivityComponent
import com.adil.pixplash.ui.base.BaseActivity
import com.adil.pixplash.ui.home.HomeViewModel

class CollectionPhotosActivity: BaseActivity<HomeViewModel>() {

    override fun provideLayoutId(): Int = R.layout.activity_home

    override fun setupView(savedInstanceState: Bundle?) {

    }

    override fun setupObservers() {
        super.setupObservers()
    }

    override fun injectDependencies(activityComponent: ActivityComponent) = activityComponent.inject(this)
}