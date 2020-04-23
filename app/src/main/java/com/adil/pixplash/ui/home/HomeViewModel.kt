package com.adil.pixplash.ui.home

import androidx.lifecycle.MutableLiveData
import com.adil.pixplash.ui.base.BaseViewModel
import com.adil.pixplash.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable

class HomeViewModel(schedulerProvider: SchedulerProvider,
                    compositeDisposable: CompositeDisposable
): BaseViewModel(schedulerProvider, compositeDisposable) {

    val data = MutableLiveData<String>()

    override fun onCreate() {

    }
}