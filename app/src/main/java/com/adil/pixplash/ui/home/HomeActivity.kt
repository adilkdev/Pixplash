package com.adil.pixplash.ui.home

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.StrictMode
import android.transition.Fade
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.adil.pixplash.R
import com.adil.pixplash.data.local.prefs.UserPreferences
import com.adil.pixplash.di.component.ActivityComponent
import com.adil.pixplash.ui.base.BaseActivity
import com.adil.pixplash.ui.home.collection.fragment.CollectionFragment
import com.adil.pixplash.ui.home.explore.ExploreFragment
import com.adil.pixplash.ui.home.setting.SettingFragment
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_explore.*
import javax.inject.Inject


class HomeActivity : BaseActivity<HomeViewModel>() {

    @Inject
    lateinit var prefs: UserPreferences

    override fun provideLayoutId(): Int  = R.layout.activity_home

    override fun setupObservers() {
        super.setupObservers()
        viewModel.data.observe(this, Observer {

        })
    }

    override fun setupView(savedInstanceState: Bundle?) {
        //setTransitions()

        /*initialize all fragments */
        val exploreFragment = ExploreFragment.newInstance()
        val collectionFragment = CollectionFragment.newInstance()
        val profileFragment = SettingFragment.newInstance()
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
                    collectionFragment.scrollToTop()
                }
                R.id.navProfile -> {

                }
                else -> {

                }
            }
        }
    }

    private fun setTransitions() {
        val fade = Fade()
        fade.excludeTarget(android.R.id.statusBarBackground, true)
        fade.excludeTarget(android.R.id.navigationBarBackground, true)
        window.enterTransition = fade
        window.exitTransition = fade
    }

    private fun setTheme() {
        when(prefs.getTheme()) {
            1 -> {
                setDarkTheme()
            } else -> {
            coordinatorLayout.setBackgroundColor(Color.WHITE)
        }
        }
    }

    private fun setDarkTheme() {
        val states = arrayOf(
            intArrayOf(android.R.attr.state_checked),
            intArrayOf(-android.R.attr.state_checked)
        )
        val colors = intArrayOf(
            Color.WHITE,
            Color.parseColor("#3d3d3d")
        )
        val myList = ColorStateList(states, colors)
        //bottomNavigation.setBackgroundColor(Color.parseColor("#160f30"))
        bottomNavigation.setBackgroundColor(Color.parseColor("#0c0b0f"))
        bottomNavigation.itemIconTintList = myList
    }

    override fun injectDependencies(activityComponent: ActivityComponent) = activityComponent.inject(this)

}
