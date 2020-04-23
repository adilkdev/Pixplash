package com.adil.pixplash.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.adil.pixplash.R
import com.adil.pixplash.di.component.ActivityComponent
import com.adil.pixplash.ui.base.BaseActivity
import com.adil.pixplash.ui.home.collection.CollectionFragment
import com.adil.pixplash.ui.home.explore.ExploreFragment
import com.adil.pixplash.ui.home.profile.ProfileFragment
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : BaseActivity<HomeViewModel>() {

    override fun provideLayoutId(): Int  = R.layout.activity_home

    override fun setupObservers() {
        super.setupObservers()
        viewModel.data.observe(this, Observer {

        })
    }

    override fun setupView(savedInstanceState: Bundle?) {
        bottomNavigation.setOnNavigationItemSelectedListener {
            val fragment: Fragment = when (it.itemId) {
                R.id.navExplore -> ExploreFragment.newInstance()
                R.id.navCollection -> CollectionFragment.newInstance()
                R.id.navProfile -> ProfileFragment.newInstance()
                else -> Fragment()
            }
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.containerLayout, fragment)
            transaction.commit()
            return@setOnNavigationItemSelectedListener true
        }
        bottomNavigation.selectedItemId = R.id.navExplore
    }

    override fun injectDependencies(activityComponent: ActivityComponent) = activityComponent.inject(this)

}
