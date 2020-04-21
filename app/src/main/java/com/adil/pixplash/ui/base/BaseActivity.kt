package com.adil.pixplash.ui.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.adil.pixplash.UnsplashApplication
import com.adil.pixplash.di.component.ActivityComponent
import com.adil.pixplash.di.component.DaggerActivityComponent
import com.adil.pixplash.di.module.ActivityModule
import com.adil.pixplash.utils.display.Toaster
import javax.inject.Inject

abstract class BaseActivity <VM: BaseViewModel> : AppCompatActivity() {

    @Inject
    lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependencies(buildActivityComponent())
        super.onCreate(savedInstanceState)
        setContentView(provideLayoutId())
        setupObservers()
        viewModel.onCreate()
        setupView(savedInstanceState)
    }

    @LayoutRes
    protected abstract fun provideLayoutId(): Int

    protected open fun setupObservers() {
        viewModel.messageStringId.observe(this, Observer {
            it.data?.run { showMessage(this) }
        })
        viewModel.messageString.observe(this, Observer {
            it.data?.run { showMessage(this) }
        })
    }

    private fun buildActivityComponent() =
        DaggerActivityComponent
            .builder()
            .applicationComponent((application as UnsplashApplication).applicationComponent)
            .activityModule(ActivityModule(this))
            .build()

    fun showMessage(message: String) = Toaster.show(applicationContext, message)

    fun showMessage(@StringRes resId: Int) = showMessage(getString(resId))

    protected abstract fun setupView(savedInstanceState: Bundle?)

    protected abstract fun injectDependencies(activityComponent: ActivityComponent)
}