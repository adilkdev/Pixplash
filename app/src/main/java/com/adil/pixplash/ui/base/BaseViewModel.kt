package com.adil.pixplash.ui.base

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adil.pixplash.R
import com.adil.pixplash.utils.common.Resource
import com.adil.pixplash.utils.network.NetworkHelper
import com.adil.pixplash.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import javax.net.ssl.HttpsURLConnection

abstract class BaseViewModel(
    protected val schedulerProvider: SchedulerProvider,
    protected  val compositeDisposable: CompositeDisposable,
    protected val networkHelper: NetworkHelper
): ViewModel() {

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    abstract fun onCreate()

    protected fun checkInternetConnectionWithMessage(): Boolean = networkHelper.isNetworkConnected()

    protected fun getNetworkError(err: Throwable?): Int {
        var id = 0
        err?.let {
            networkHelper.castToNetworkError(it).apply {
                id = when (status) {
                    -1 -> R.string.network_default_error
                    0 -> R.string.server_connection_error
                    HttpsURLConnection.HTTP_UNAUTHORIZED -> {
                        //forcedLogoutUser()
                        R.string.permission_denied
                    }
                    HttpsURLConnection.HTTP_INTERNAL_ERROR ->
                        R.string.network_internal_error
                    HttpsURLConnection.HTTP_UNAVAILABLE ->
                        R.string.network_server_not_available
                    else -> R.string.network_default_error
                }
            }
        }
        return id
    }

}