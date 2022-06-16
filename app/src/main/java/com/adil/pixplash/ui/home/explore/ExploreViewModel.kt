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
import kotlinx.coroutines.launch

class ExploreViewModel(
    coroutineDispatcherProvider: CoroutineDispatcherProvider,
    networkHelper: NetworkHelper,
    private val photoRepository: PhotoRepository
) : BaseViewModel(coroutineDispatcherProvider, networkHelper) {

    val loading: MutableLiveData<Boolean> = MutableLiveData(false)
    val photos: MutableLiveData<Resource<List<Photo>>> = MutableLiveData()
    val error: MutableLiveData<Int> = MutableLiveData()
    val randomPhoto: MutableLiveData<String> = MutableLiveData()

    companion object {
        private val TAG = this::class.simpleName
        private const val INITIAL_PAGE_NUMBER = 1
    }

    /** stores next page no. to be fetched, for the very time the page no. to be fetched is 1 */
    private var page = INITIAL_PAGE_NUMBER

    /** We consider the default category as latest */
    private var orderByCategory: PhotoCategory = PhotoCategory.LATEST

    private fun getPhotosFromApi(pageNo: Int = page, orderBy: PhotoCategory = orderByCategory) {
        Log.e("adilLogging", "page = $pageNo  orderBy = $orderBy")
        loading.value = true
        randomPhoto()
        viewModelScope.launch(coroutineDispatcherProvider.io) {
            val result = photoRepository.fetchPhotos(page = pageNo, orderBy = orderBy.value)
            try {
                photos.postValue(Resource.success(result))
                page++
            } catch (exception: Exception) {
                exception.localizedMessage?.let { Log.e(TAG, it) }
                error.postValue(getNetworkError(exception))
            } finally {
                loading.postValue(false)
            }

        }
    }

    private fun randomPhoto() {
        viewModelScope.launch(coroutineDispatcherProvider.io) {
            val result = photoRepository.fetchOneRandomPhoto()
            try {
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

    fun updateState(orderBy: String) {
        this.page = INITIAL_PAGE_NUMBER
        this.orderByCategory = when (orderBy) {
            "latest" -> PhotoCategory.LATEST
            "oldest" -> PhotoCategory.OLDEST
            "popular" -> PhotoCategory.POPULAR
            else -> PhotoCategory.LATEST
        }
    }

    fun onLoadMore() {
        Log.e(TAG, "onLoadMore: ${loading.value}")
        if (loading.value !== null || loading.value == false) getPhotosFromApi()
    }

    fun getPage() = page - 1

}