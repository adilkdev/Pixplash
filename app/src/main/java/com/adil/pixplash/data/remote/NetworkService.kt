package com.adil.pixplash.data.remote

import com.adil.pixplash.data.local.db.entity.Photo
import com.adil.pixplash.data.remote.Endpoints
import com.adil.pixplash.data.remote.response.*
import com.adil.pixplash.data.remote.response.Collection
import com.adil.pixplash.utils.AppConstants
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.*
import javax.inject.Singleton

@Singleton
interface NetworkService {

    @GET(Endpoints.PHOTOS)
    fun fetchPhotos(
        @Query("client_id") clientId: String = AppConstants.ACCESS_KEY,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("order_by") orderBy: String
    ): Single<List<Photo>>

    @GET(Endpoints.SEARCH_PHOTOS)
    fun searchPhotos(
        @Query("client_id") clientId: String = AppConstants.ACCESS_KEY,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("query") query: String
    ): Single<SearchPhotoResponse>

    @GET(Endpoints.RANDOM_PHOTO)
    fun fetchOneRandomPhoto(
        @Query("client_id") clientId: String = AppConstants.ACCESS_KEY
    ): Single<Photo>

    @GET(Endpoints.PHOTO_DETAIL+"{id}")
    fun fetchPhotoDetails(@Path("id") id: String,
        @Query("client_id") clientId: String = AppConstants.ACCESS_KEY
    ): Single<PhotoDetailResponse>

    @GET(Endpoints.COLLECTIONS)
    fun fetchCollections(
        @Query("client_id") clientId: String = AppConstants.ACCESS_KEY,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ) : Single<List<Collection>>

    @GET(Endpoints.SEARCH_COLLECTION)
    fun searchCollections(
        @Query("client_id") clientId: String = AppConstants.ACCESS_KEY,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("query") query: String
    ) : Single<SearchCollectionResponse>

    @GET(Endpoints.COLLECTION_FEATURED)
    fun fetchFeaturedCollections(
        @Query("client_id") clientId: String = AppConstants.ACCESS_KEY,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ) : Single<List<Collection>>

    @GET(Endpoints.COLLECTION_PHOTOS+"{id}/photos")
    fun fetchCollectionPhotos(
        @Path("id") id: String,
        @Query("client_id") clientId: String = AppConstants.ACCESS_KEY,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ) : Single<List<Photo>>

    /*@POST(Endpoints.DUMMY)
    fun doDummyCall(
        @Body request: DummyRequest,
        @Header(Networking.HEADER_API_KEY) apiKey: String = Networking.API_KEY // default value set when Networking create is called
    ): Single<DummyResponse>*/

    /*
     * Example to add other headers
     *
     *  @POST(Endpoints.DUMMY_PROTECTED)
        fun sampleDummyProtectedCall(
            @Body request: DummyRequest,
            @Header(Networking.HEADER_USER_ID) userId: String, // pass using UserRepository
            @Header(Networking.HEADER_ACCESS_TOKEN) accessToken: String, // pass using UserRepository
            @Header(Networking.HEADER_API_KEY) apiKey: String = Networking.API_KEY // default value set when Networking create is called
        ): Single<DummyResponse>
     */
}