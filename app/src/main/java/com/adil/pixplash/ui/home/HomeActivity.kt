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
import kotlinx.android.synthetic.main.fragment_explore.*


class HomeActivity : BaseActivity<HomeViewModel>() {

    override fun provideLayoutId(): Int  = R.layout.activity_home

    override fun setupObservers() {
        super.setupObservers()
        viewModel.data.observe(this, Observer {

        })
    }

    override fun setupView(savedInstanceState: Bundle?) {
        /*initialize all fragments */
        val exploreFragment = ExploreFragment.newInstance()
        val collectionFragment = CollectionFragment.newInstance()
        val profileFragment = ProfileFragment.newInstance()
        var activeFragment: Fragment = exploreFragment

        supportFragmentManager.beginTransaction().add(R.id.containerLayout, profileFragment,"3").hide(profileFragment).commit()
        supportFragmentManager.beginTransaction().add(R.id.containerLayout, collectionFragment, "2").hide(collectionFragment).commit()
        supportFragmentManager.beginTransaction().add(R.id.containerLayout, exploreFragment, "1").commit()

        bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navExplore -> {
                    supportFragmentManager.beginTransaction().hide(activeFragment).show(exploreFragment).commit()
                    activeFragment = exploreFragment
                }
                R.id.navCollection -> {
                    supportFragmentManager.beginTransaction().hide(activeFragment).show(collectionFragment).commit()
                    activeFragment = collectionFragment
                }
                R.id.navProfile -> {
                    supportFragmentManager.beginTransaction().hide(activeFragment).show(profileFragment).commit()
                    activeFragment = profileFragment
                }
                else -> {
                    supportFragmentManager.beginTransaction().hide(activeFragment).show(exploreFragment).commit()
                    activeFragment = exploreFragment
                }
            }
            return@setOnNavigationItemSelectedListener true
        }
        bottomNavigation.selectedItemId = R.id.navExplore

        bottomNavigation.setOnNavigationItemReselectedListener {
            when (it.itemId) {
                R.id.navExplore -> {
                    exploreFragment.scrollToTop()
                }
                R.id.navCollection -> {

                }
                R.id.navProfile -> {

                }
                else -> {

                }
            }
        }
    }

    override fun injectDependencies(activityComponent: ActivityComponent) = activityComponent.inject(this)

}
