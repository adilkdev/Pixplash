package com.adil.pixplash.ui.home.search

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.fonts.Font
import android.os.Bundle
import android.transition.Fade
import android.view.Choreographer.getInstance
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.adil.pixplash.R
import com.adil.pixplash.di.component.ActivityComponent
import com.adil.pixplash.ui.base.BaseActivity
import com.adil.pixplash.ui.home.HomeViewModel
import com.adil.pixplash.ui.home.search.photo.SearchPhotoFragment
import kotlinx.android.synthetic.main.activity_search.*


class SearchActivity: BaseActivity<HomeViewModel>() {

    override fun provideLayoutId(): Int = R.layout.activity_search

    override fun setupView(savedInstanceState: Bundle?) {
        setTransition()
        setupTabLayout()
        tvCancel.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupTabLayout() {
        tabLayout.setupWithViewPager(viewPager)
        val viewPagerAdapter = PagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragment(SearchPhotoFragment(), "Photos")
        viewPagerAdapter.addFragment(SearchPhotoFragment(), "Collections")
        viewPager.adapter = viewPagerAdapter
        changeTabsFont()
    }

    private fun changeTabsFont() {
        val vg = tabLayout.getChildAt(0) as ViewGroup
        val tabsCount = vg.childCount
        val typeface = Typeface.createFromAsset(applicationContext.assets, "source_sans_pro_regular.ttf")
        for (j in 0 until tabsCount) {
            val vgTab = vg.getChildAt(j) as ViewGroup
            val tabChildCount = vgTab.childCount
            for (i in 0 until tabChildCount) {
                val tabViewChild: View = vgTab.getChildAt(i)
                if (tabViewChild is TextView) {
                    tabViewChild.setTypeface(typeface, Typeface.NORMAL)
                    tabViewChild.setTextColor(Color.BLACK)
                    tabViewChild.letterSpacing = 0.05f
                }
            }
        }
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

    class PagerAdapter(fragmentManager: FragmentManager) :
        FragmentStatePagerAdapter(fragmentManager, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        private var fragments = mutableListOf<Fragment>()
        private var titles = mutableListOf<String>()

        fun addFragment(fragment: Fragment, title: String) {
            fragments.add(fragment)
            titles.add(title)
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }
    }


}