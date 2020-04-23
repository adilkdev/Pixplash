package com.adil.pixplash.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.adil.pixplash.PixplashApplication
import com.adil.pixplash.di.component.DaggerFragmentComponent
import com.adil.pixplash.di.component.FragmentComponent
import com.adil.pixplash.di.module.FragmentModule
import com.adil.pixplash.utils.display.Toaster
import javax.inject.Inject

abstract class BaseFragment<VM: BaseViewModel> : Fragment() {

    @Inject
    lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependencies(buildFragmentComponent())
        super.onCreate(savedInstanceState)
        setupObservers()
        viewModel.onCreate()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(provideLayoutId(), container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(view)
    }

    @LayoutRes
    protected abstract fun provideLayoutId(): Int

    protected open fun setupObservers() {
//        viewModel.messageStringId.observe(this, Observer {
//            //it.data?.run { showMessage(this) }
//        })
//        viewModel.messageString.observe(this, Observer {
//            //it.data?.run { showMessage(this) }
//        })
    }

    private fun buildFragmentComponent() =
        DaggerFragmentComponent
            .builder()
            .applicationComponent((context!!.applicationContext as PixplashApplication).applicationComponent)
            .fragmentModule(FragmentModule(this))
            .build()

    fun showMessage(message: String) = Toaster.show(context!!, message)

    fun showMessage(@StringRes resId: Int) = showMessage(getString(resId))

    protected abstract fun setupView(savedInstanceState: View)

    protected abstract fun injectDependencies(fragmentComponent: FragmentComponent)

}