package com.adil.pixplash.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.adil.pixplash.R
import com.adil.pixplash.ui.base.BaseActivity
import com.adil.pixplash.ui.home.collection.fragment.CollectionFragment
import com.adil.pixplash.ui.home.explore.ExploreFragment
import com.adil.pixplash.ui.home.setting.SettingFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*

@AndroidEntryPoint
class HomeActivity : BaseActivity<HomeViewModel>() {

    override fun provideLayoutId(): Int = R.layout.activity_home
    private lateinit var activeFragment: Fragment
    private val exploreFragment by lazy { ExploreFragment.newInstance() }
    private val collectionFragment by lazy { CollectionFragment.newInstance() }
    private val settingFragment by lazy { SettingFragment.newInstance() }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.data.observe(this, Observer {

        })
    }

    override fun setupView(savedInstanceState: Bundle?) {
        /** setup all fragments */
        setupFragments()

        /** setup on navigation item selected listener */
        bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navExplore -> setActiveFragment(exploreFragment)
                R.id.navCollection -> setActiveFragment(collectionFragment)
                R.id.navProfile -> setActiveFragment(settingFragment)
                else -> setActiveFragment(exploreFragment)
            }
            return@setOnNavigationItemSelectedListener true
        }
        bottomNavigation.selectedItemId = R.id.navExplore

        bottomNavigation.setOnNavigationItemReselectedListener {
            when (it.itemId) {
                R.id.navExplore -> exploreFragment.scrollToTop()
                R.id.navCollection -> collectionFragment.scrollToTop()
                R.id.navProfile -> {}
                else -> {}
            }
        }
    }

    private fun setActiveFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().hide(activeFragment)
            .show(fragment).commit()
        activeFragment = fragment
    }

    private fun setupFragments() {
        activeFragment = exploreFragment

        supportFragmentManager.beginTransaction().add(R.id.containerLayout, settingFragment, "3")
            .hide(settingFragment).commit()
        supportFragmentManager.beginTransaction().add(R.id.containerLayout, collectionFragment, "2")
            .hide(collectionFragment).commit()
        supportFragmentManager.beginTransaction().add(R.id.containerLayout, exploreFragment, "1")
            .commit()
    }

}
