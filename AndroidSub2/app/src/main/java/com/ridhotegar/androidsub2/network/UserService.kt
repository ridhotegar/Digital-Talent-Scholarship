package com.ridhotegar.androidsub2.network

import com.ridhotegar.androidsub2.model.response.SearchResponse
import com.ridhotegar.androidsub2.model.response.UserDetailResponse
import com.ridhotegar.androidsub2.model.response.UserItemResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {

    @GET("search/users")
    fun findUsers(@Query("q") query: String?): Call<SearchResponse>

    @GET("users/{username}")
    fun getUserDetailByUsername(@Path(value = "username") username: String?): Call<UserDetailResponse>

    @GET("users/{username}/followers")
    fun getUserFollowersByUsername(@Path(value = "username") username: String?): Call<List<UserItemResponse>>

    @GET("users/{username}/following")
    fun getUserFollowingByUsername(@Path(value = "username") username: String?): Call<List<UserItemResponse>>

}