package com.adil.pixplash.data.repository

import com.adil.pixplash.data.local.db.DatabaseService
import com.adil.pixplash.data.local.db.entity.Photo
import com.adil.pixplash.data.remote.NetworkService
import com.adil.pixplash.data.remote.response.Collection
import com.adil.pixplash.data.remote.response.PhotoDetailResponse
import com.adil.pixplash.data.remote.response.SearchCollectionResponse
import com.adil.pixplash.data.remote.response.SearchPhotoResponse
import com.adil.pixplash.ui.home.explore.SortBy
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * updated code will go in this branch
 */

@Singleton
class PhotoRepository @Inject constructor(
    private val networkService: NetworkService,
    private val databaseService: DatabaseService,
    private val job: CompletableJob
) {

    companion object {
        const val pageSize = 30
    }

    suspend fun fetchPhotos(page: Int = 1, itemsPerPage: Int = pageSize, orderBy: String = SortBy.LATEST.value)
            : List<Photo> =
        networkService.fetchPhotos(page = page, perPage = itemsPerPage, orderBy = orderBy)

    suspend fun searchPhotos(page: Int = 1, itemsPerPage: Int = pageSize, query: String = "")
            : SearchPhotoResponse =
        networkService.searchPhotos(page = page, perPage = itemsPerPage, query = query)

    suspend fun fetchOneRandomPhoto(): Photo = networkService.fetchOneRandomPhoto()

    suspend fun fetchPhotoDetails(photoId: String): PhotoDetailResponse =
        networkService.fetchPhotoDetails(photoId)

    suspend fun fetchCollections(page: Int = 1, itemsPerPage: Int = pageSize): List<Collection> =
        networkService.fetchCollections(page = page, perPage = itemsPerPage)

    suspend fun searchCollections(
        page: Int = 1,
        itemsPerPage: Int = pageSize,
        query: String = ""
    ): SearchCollectionResponse =
        networkService.searchCollections(page = page, perPage = itemsPerPage, query = query)

    suspend fun fetchFeaturedCollections(
        page: Int = 1,
        itemsPerPage: Int = pageSize
    ): List<Collection> =
        networkService.fetchFeaturedCollections(page = page, perPage = itemsPerPage)

    suspend fun fetchCollectionPhoto(
        collectionId: String,
        page: Int = 1,
        itemsPerPage: Int = pageSize
    ): List<Photo> =
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

    private suspend fun gettingPhotos(photoType: String) =
        databaseService.exploreDao().getAllPhotosFromDB(photoType)

}