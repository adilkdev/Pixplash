package com.adil.pixplash.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adil.pixplash.utils.common.Resource
import io.reactivex.disposables.CompositeDisposable

abstract class BaseViewModel(
    protected  val compositeDisposable: CompositeDisposable
): ViewModel() {

    val messageStringId: MutableLiveData<Resource<Int>> = MutableLiveData()
    val messageString: MutableLiveData<Resource<String>> = MutableLiveData()

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    abstract fun onCreate()

}