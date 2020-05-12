package com.adil.pixplash.ui.home.collection

import android.os.Bundle
import android.transition.Fade
import com.adil.pixplash.R
import com.adil.pixplash.di.component.ActivityComponent
import com.adil.pixplash.ui.base.BaseActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_collection_photos.*

class CollectionPhotosActivity: BaseActivity<CollectionPhotosViewModel>() {

    private var coverPhotoUrl: String = ""
    private var extras: Bundle? = null

    override fun provideLayoutId(): Int = R.layout.activity_collection_photos

    override fun setupView(savedInstanceState: Bundle?) {
        extras = intent.extras
        setTransitions()
        coverPhotoUrl = extras?.getString("cover").toString()
        Picasso.get().load(coverPhotoUrl).into(ivCover)
        tvBigTitle.text = extras?.getString("title")
    }

    private fun setTransitions() {
        val fade = Fade()
        fade.excludeTarget(android.R.id.statusBarBackground, true)
        fade.excludeTarget(android.R.id.navigationBarBackground, true)
        window.enterTransition = fade
        window.exitTransition = fade
    }

    override fun setupObservers() {
        super.setupObservers()
    }

    override fun injectDependencies(activityComponent: ActivityComponent) = activityComponent.inject(this)
}