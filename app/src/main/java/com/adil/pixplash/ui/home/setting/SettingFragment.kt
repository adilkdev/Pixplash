package com.adil.pixplash.ui.home.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.adil.pixplash.R
import com.adil.pixplash.di.component.FragmentComponent
import com.adil.pixplash.ui.base.BaseFragment
import com.adil.pixplash.ui.home.HomeViewModel
import kotlinx.android.synthetic.main.fragment_about.*

class SettingFragment: BaseFragment<HomeViewModel>() {

    companion object {
        val TAG = "HomeFragment"

        fun newInstance() : SettingFragment {
            val args = Bundle()
            val fragment = SettingFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun provideLayoutId(): Int = R.layout.fragment_about

    override fun setupView(savedInstanceState: View) {
        profileLayout.setOnClickListener {
            val link = "https://www.instagram.com/iadilkhan__/"
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            startActivity(browserIntent)
        }
    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.data.observe(this, Observer {

        })
    }

    override fun injectDependencies(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)
}