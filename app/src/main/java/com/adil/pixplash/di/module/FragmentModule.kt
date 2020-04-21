package com.adil.pixplash.di.module

import androidx.lifecycle.ViewModelProviders
import com.adil.pixplash.ui.base.BaseFragment
import com.adil.pixplash.ui.home.HomeViewModel
import com.adil.pixplash.utils.ViewModelProviderFactory
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable

@Module
class FragmentModule(private val fragment: BaseFragment<*>) {

    @Provides
    fun provideHomeViewModel(
        compositeDisposable: CompositeDisposable
    ) : HomeViewModel =
        ViewModelProviders.of(fragment,
            ViewModelProviderFactory(HomeViewModel::class) {
                HomeViewModel(compositeDisposable)
            }).get(HomeViewModel::class.java)

}