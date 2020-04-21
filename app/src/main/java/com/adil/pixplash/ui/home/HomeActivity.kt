package com.adil.pixplash.ui.home

import android.graphics.Outline
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewOutlineProvider
import androidx.lifecycle.Observer
import com.adil.pixplash.R
import com.adil.pixplash.di.component.ActivityComponent
import com.adil.pixplash.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : BaseActivity<HomeViewModel>() {

    override fun provideLayoutId(): Int  = R.layout.activity_home

    override fun setupObservers() {
        super.setupObservers()
        viewModel.data.observe(this, Observer {

        })
    }

    override fun setupView(savedInstanceState: Bundle?) {
        val viewOutlineProvider: ViewOutlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                val cornerRadiusDP = 35f
                val cornerRadius = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    cornerRadiusDP,
                    resources.displayMetrics
                )
                outline.setRoundRect(0, - cornerRadius.toInt(), view.width, ((view.height) ).toInt(), cornerRadius)
            }
        }
        relative.outlineProvider = viewOutlineProvider
        relative.clipToOutline = true
    }

    override fun injectDependencies(activityComponent: ActivityComponent) = activityComponent.inject(this)
}
