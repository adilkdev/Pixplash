package com.adil.pixplash.ui.home.explore


import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.adil.pixplash.data.local.db.entity.Photo
import com.adil.pixplash.data.repository.PhotoRepository
import com.adil.pixplash.ui.base.BaseViewModel
import com.adil.pixplash.utils.common.Resource
import com.adil.pixplash.utils.dispatcher.CoroutineDispatcherProvider
import com.adil.pixplash.utils.network.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject
import javax.net.ssl.SSLHandshakeException

@HiltViewModel
class ExploreViewModel @Inject constructor(
    coroutineDispatcherProvider: CoroutineDispatcherProvider,
    networkHelper: NetworkHelper,
    private val photoRepository: PhotoRepository
) : BaseViewModel(coroutineDispatcherProvider, networkHelper) {

    val loading: MutableLiveData<Boolean> = MutableLiveData(false)
    val photos: MutableLiveData<Resource<List<Photo>>> = MutableLiveData()
    val error: MutableLiveData<Int> = MutableLiveData()
    val randomPhoto: MutableLiveData<String> = MutableLiveData()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e(TAG, "$exception coroutineExceptionHandler")
    }

    companion object {
        private val TAG = "ExploreViewModel"
        private const val INITIAL_PAGE_NUMBER = 1
    }

    init {
        randomPhoto()
    }

    /** stores next page no. to be fetched, for the very time the page no. to be fetched is 1 */
    private var page = INITIAL_PAGE_NUMBER

    /** We consider the default category as latest */
    private var sortBy: SortBy = SortBy.LATEST

    private fun getPhotosFromApi(pageNo: Int = page, orderBy: SortBy = sortBy) {
        Log.e("adilLogging", "page = $pageNo  orderBy = $orderBy")
        loading.postValue(true)
        viewModelScope.launch(coroutineDispatcherProvider.io + coroutineExceptionHandler) {
            try {
                val result = photoRepository.fetchPhotos(page = pageNo, orderBy = orderBy.value)
                photos.postValue(Resource.success(result))
                page++
            } catch (exception: Exception) {
                exception.localizedMessage?.let { println("exception $it") }
                error.postValue(getNetworkError(exception))
            } catch (exception: UnknownHostException) {
                exception.localizedMessage?.let { println("unknown host $it") }
                error.postValue(getNetworkError(exception))
            } catch (exception: SSLHandshakeException) {
                exception.localizedMessage?.let { println("ssl $it") }
                error.postValue(getNetworkError(exception))
            } finally {
                loading.postValue(false)
            }

        }
    }

    private fun randomPhoto() {
        viewModelScope.launch(coroutineDispatcherProvider.io) {
            try {
                val result = photoRepository.fetchOneRandomPhoto()
                randomPhoto.postValue(result.urls.regular)
            } catch (exception: Exception) {
                exception.localizedMessage?.let { Log.e(TAG, it) }
            }
        }
    }

    fun savePhotos(list: List<Photo>, photoType: String) =
        photoRepository.savePhotos(list, photoType)

    fun removePhotos(photoType: String) =
        photoRepository.removePhotos(photoType)

    fun updateState(sortBy: SortBy) {
        this.page = INITIAL_PAGE_NUMBER
        this.sortBy = sortBy
    }

    fun onLoadMore() {
        Log.e(TAG, "onLoadMore: ${loading.value}")
        if (loading.value !== null || loading.value == false) getPhotosFromApi()
    }

    fun getPage() = page - 1

}