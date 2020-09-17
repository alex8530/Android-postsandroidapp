package com.example.postsandroidapp.model.api

import com.example.postsandroidapp.model.dataapi.MessageDataAPi
import com.example.postsandroidapp.model.dataapi.PostsDataAPI
import retrofit2.Call
import retrofit2.http.*

@JvmSuppressWildcards
interface PostsAPI {

    @Headers(
        "Accept:application/json"
    )
    @GET("account/location/index")
    fun getPostsList(
        @Header("Authorization") token: String,
        @Query("page") page: Int

    ): Call<PostsDataAPI>

    @Headers(
        "Accept:application/json",
        "Content-Type:application/x-www-form-urlencoded"
    )
    @DELETE("account/location/{location_id}/delete")
    fun removePosts(
        @Path("location_id") location_id: Int,
        @Header("Authorization") token: String
    ): Call<MessageDataAPi>


    @FormUrlEncoded
    @Headers(
        "Accept:application/json",
        "Content-Type:application/x-www-form-urlencoded"
    )
    @POST("account/location/store")
    fun addPost(
        @Header("Authorization") token: String,
        @Field("title") title: String,
        @Field("lat") lat: Double,
        @Field("lng") lng: Double
    ): Call<MessageDataAPi>


}
