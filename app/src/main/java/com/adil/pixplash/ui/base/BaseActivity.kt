package com.adil.pixplash.ui.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.adil.pixplash.utils.display.Toaster
import javax.inject.Inject

abstract class BaseActivity <VM: BaseViewModel> : AppCompatActivity() {

    @Inject
    lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(provideLayoutId())
        setupObservers()
        setupView(savedInstanceState)
    }

    @LayoutRes
    protected abstract fun provideLayoutId(): Int

    protected open fun setupObservers() {}

    protected abstract fun setupView(savedInstanceState: Bundle?)

    fun showMessage(message: String) = Toaster.show(applicationContext, message)

    fun showMessage(@StringRes resId: Int) = showMessage(getString(resId))
}