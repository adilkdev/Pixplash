package com.adil.pixplash.ui.base

import androidx.lifecycle.ViewModel
import com.adil.pixplash.R
import com.adil.pixplash.utils.dispatcher.CoroutineDispatcherProvider
import com.adil.pixplash.utils.network.NetworkHelper
import javax.net.ssl.HttpsURLConnection

abstract class BaseViewModel(
    protected val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    protected val networkHelper: NetworkHelper
): ViewModel() {

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