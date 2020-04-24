package com.adil.pixplash.data.remote

import com.adil.pixplash.data.remote.Endpoints
import com.adil.pixplash.data.remote.response.PhotoResponse
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
    ): Single<List<PhotoResponse>>

    @GET(Endpoints.RANDOM_PHOTO)
    fun fetchOneRandomPhoto(
        @Query("client_id") clientId: String = AppConstants.ACCESS_KEY
    ): Single<PhotoResponse>

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