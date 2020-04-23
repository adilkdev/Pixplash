package com.adil.pixplash.ui.home

import androidx.lifecycle.MutableLiveData
import com.adil.pixplash.ui.base.BaseViewModel
import com.adil.pixplash.utils.network.NetworkHelper
import com.adil.pixplash.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable

class HomeViewModel(schedulerProvider: SchedulerProvider,
                    compositeDisposable: CompositeDisposable,
                    networkHelper: NetworkHelper
): BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    val data = MutableLiveData<String>()

    override fun onCreate() {

    }
}