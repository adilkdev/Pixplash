package com.adil.pixplash.ui.home.search

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.Fade
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.adil.pixplash.R
import com.adil.pixplash.di.component.ActivityComponent
import com.adil.pixplash.ui.base.BaseActivity
import com.adil.pixplash.ui.home.HomeViewModel
import com.adil.pixplash.ui.home.search.collection.SearchCollectionFragment
import com.adil.pixplash.ui.home.search.photo.SearchPhotoFragment
import kotlinx.android.synthetic.main.activity_search.*


class SearchActivity: BaseActivity<HomeViewModel>() {

    private lateinit var photoQueryListener: PhotoQueryListener
    private lateinit var collectionQueryListener: CollectionQueryListener

    override fun provideLayoutId(): Int = R.layout.activity_search

    override fun setupView(savedInstanceState: Bundle?) {
        setTransition()
        setupTabLayout()
        tvCancel.setOnClickListener {
            onBackPressed()
        }
        ivClear.setOnClickListener {
            etSearch.text = Editable.Factory.getInstance().newEditable("")
        }
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrBlank()) {
                    ivClear.visibility = View.VISIBLE
                } else
                    ivClear.visibility = View.INVISIBLE
            }

        })
        etSearch.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun performSearch() {
        etSearch.clearFocus()
        val imm =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(etSearch.windowToken, 0)
        //...perform search
        photoQueryListener.onSearch(etSearch.text.toString())
        collectionQueryListener.onSearch(etSearch.text.toString())
    }

    private fun setupTabLayout() {
        tabLayout.setupWithViewPager(viewPager)
        val viewPagerAdapter = PagerAdapter(supportFragmentManager)
        val searchPhotoFragment = SearchPhotoFragment()
        this.photoQueryListener = searchPhotoFragment
        val searchCollectionFragment = SearchCollectionFragment()
        this.collectionQueryListener = searchCollectionFragment
        viewPagerAdapter.addFragment(searchPhotoFragment, "Photos")
        viewPagerAdapter.addFragment(searchCollectionFragment, "Collections")
        viewPager.adapter = viewPagerAdapter
        //tabLayout.setTabTextColors(resources.getColor(R.color.colorLightGray), resources.getColor(R.color.colorPrimaryDark))
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
                    tabViewChild.setTypeface(typeface, Typeface.BOLD)
                    //tabViewChild.setTextColor(Color.BLACK)
                    tabViewChild.letterSpacing = 0.1f
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

interface PhotoQueryListener {
    fun onSearch(query: String)
}

interface CollectionQueryListener {
    fun onSearch(query: String)
}