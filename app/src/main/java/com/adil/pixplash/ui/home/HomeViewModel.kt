package com.adil.pixplash.ui.home

import androidx.lifecycle.MutableLiveData
import com.adil.pixplash.ui.base.BaseViewModel
import io.reactivex.disposables.CompositeDisposable

class HomeViewModel(compositeDisposable: CompositeDisposable): BaseViewModel(compositeDisposable) {

    val data = MutableLiveData<String>()

    override fun onCreate() {

    }
}