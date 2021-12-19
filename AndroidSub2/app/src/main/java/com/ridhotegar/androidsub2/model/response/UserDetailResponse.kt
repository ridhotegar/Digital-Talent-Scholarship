package com.ridhotegar.androidsub2.model.response

import com.google.gson.annotations.SerializedName

data class UserDetailResponse(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("login") val username: String? = null,
    @SerializedName("avatar_url") val avatarUrl: String? = null,
    @SerializedName("location") val location: String? = null,
    @SerializedName("company") val company: String? = null,
    @SerializedName("public_repos") val publicRepos: Int? = null,
    @SerializedName("followers") val follower: Int? = null,
    @SerializedName("following") val following: Int? = null
)