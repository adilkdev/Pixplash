package com.adil.pixplash.di.module

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.adil.pixplash.di.ActivityScope
import com.adil.pixplash.ui.base.BaseActivity
import com.adil.pixplash.ui.home.HomeViewModel
import com.adil.pixplash.utils.ViewModelProviderFactory
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable

@Module
class ActivityModule(private val activity: BaseActivity<*>) {

    @ActivityScope
    @Provides
    fun provideContext(): Context = activity

    @Provides
    fun provideHomeViewModel(
        compositeDisposable: CompositeDisposable
    ) : HomeViewModel =
        ViewModelProviders.of(activity,
            ViewModelProviderFactory(HomeViewModel::class) {
                HomeViewModel(compositeDisposable)
            }).get(HomeViewModel::class.java)

}