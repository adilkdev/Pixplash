package com.adil.pixplash.ui.home.search

import android.os.Bundle
import android.transition.Fade
import com.adil.pixplash.R
import com.adil.pixplash.di.component.ActivityComponent
import com.adil.pixplash.ui.base.BaseActivity
import com.adil.pixplash.ui.home.HomeViewModel

class SearchActivity: BaseActivity<HomeViewModel>() {

    override fun provideLayoutId(): Int = R.layout.activity_search

    override fun setupView(savedInstanceState: Bundle?) {
        setTransition()
    }

    override fun injectDependencies(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    private fun setTransition() {
        val fade = Fade()
        fade.excludeTarget(android.R.id.statusBarBackground, true)
        fade.excludeTarget(android.R.id.navigationBarBackground, true)
        window.enterTransition = fade
        window.exitTransition = fade
    }


}