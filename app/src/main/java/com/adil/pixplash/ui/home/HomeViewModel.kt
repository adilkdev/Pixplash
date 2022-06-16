package com.adil.pixplash.ui.home

import androidx.lifecycle.MutableLiveData
import com.adil.pixplash.ui.base.BaseViewModel
import com.adil.pixplash.utils.dispatcher.CoroutineDispatcherProvider
import com.adil.pixplash.utils.network.NetworkHelper

class HomeViewModel(coroutineDispatcherProvider: CoroutineDispatcherProvider,
                    networkHelper: NetworkHelper
): BaseViewModel(coroutineDispatcherProvider, networkHelper) {

    val data = MutableLiveData<String>()

}