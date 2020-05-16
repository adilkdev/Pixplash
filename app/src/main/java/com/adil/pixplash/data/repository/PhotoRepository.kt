package com.adil.pixplash.data.repository

import android.util.Log
import com.adil.pixplash.data.local.db.DatabaseService
import com.adil.pixplash.data.local.db.entity.Photo
import com.adil.pixplash.data.remote.NetworkService
import com.adil.pixplash.data.remote.response.*
import com.adil.pixplash.data.remote.response.Collection
import io.reactivex.Single
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepository @Inject constructor(private val networkService: NetworkService,
                                          private val databaseService: DatabaseService,
                                          private val job: CompletableJob
) {

    companion object {
        const val pageSize = 30
    }

    fun fetchPhotos(page: Int = 1, itemsPerPage: Int = pageSize, orderBy: String = "latest")
            : Single<List<Photo>> =
        networkService.fetchPhotos(page = page, perPage = itemsPerPage, orderBy = orderBy)

    fun searchPhotos(page: Int = 1, itemsPerPage: Int = pageSize, query: String = "")
            : Single<SearchPhotoResponse> =
        networkService.searchPhotos(page = page, perPage = itemsPerPage, query = query)

    fun fetchOneRandomPhoto(): Single<Photo> = networkService.fetchOneRandomPhoto()

    fun fetchPhotoDetails(photoId: String): Single<PhotoDetailResponse> =
        networkService.fetchPhotoDetails(photoId)

    fun fetchCollections(page: Int = 1, itemsPerPage: Int = pageSize) : Single<List<Collection>> =
        networkService.fetchCollections(page = page, perPage = itemsPerPage)

    fun searchCollections(page: Int = 1, itemsPerPage: Int = pageSize, query: String = "") : Single<SearchCollectionResponse> =
        networkService.searchCollections(page = page, perPage = itemsPerPage, query = query)

    fun fetchFeaturedCollections(page: Int = 1, itemsPerPage: Int = pageSize) : Single<List<Collection>> =
        networkService.fetchFeaturedCollections(page = page, perPage = itemsPerPage)

    fun fetchCollectionPhoto(collectionId: String, page: Int = 1, itemsPerPage: Int = pageSize) : Single<List<Photo>> =
        networkService.fetchCollectionPhotos(id = collectionId, page = page, perPage = itemsPerPage)

    fun savePhotos(photos: List<Photo>, photoType: String) {
        CoroutineScope(Dispatchers.IO + job).launch {
            photos.map {
                it.photoType = photoType
            }
            databaseService.exploreDao().addImageList(photos)
        }
    }

    fun removePhotos(photoType: String) {
        CoroutineScope(Dispatchers.IO + job).launch {
            databaseService.exploreDao().removePhotos(photoType)
        }
    }

    fun getAllPhotosFromDB(photoType: String): List<Photo> {
        lateinit var photos: List<Photo>
        runBlocking(Dispatchers.IO + job) {
            val job = async { gettingPhotos(photoType) }
            photos = job.await()
        }
        return photos
    }

    private suspend fun gettingPhotos(photoType: String) = databaseService.exploreDao().getAllPhotosFromDB(photoType)

}