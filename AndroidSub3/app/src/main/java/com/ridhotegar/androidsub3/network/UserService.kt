package com.ridhotegar.androidsub3.network

import com.ridhotegar.androidsub3.model.response.SearchResponse
import com.ridhotegar.androidsub3.model.response.UserDetailResponse
import com.ridhotegar.androidsub3.model.response.UserItemResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {

    @GET("search/users?per_page=5")
    fun findUsers(@Query("q") query: String?): Call<SearchResponse>

    @GET("users/{username}")
    fun getUserDetailByUsername(@Path(value = "username") username: String?): Call<UserDetailResponse>

    @GET("users/{username}/followers?per_page=5")
    fun getUserFollowersByUsername(@Path(value = "username") username: String?): Call<List<UserItemResponse>>

    @GET("users/{username}/following?per_page=5")
    fun getUserFollowingByUsername(@Path(value = "username") username: String?): Call<List<UserItemResponse>>

}