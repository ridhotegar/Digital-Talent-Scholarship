package com.ridhotegar.androidsub2.model.response

import com.google.gson.annotations.SerializedName

data class UserItemResponse(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("login") val username: String? = null,
    @SerializedName("avatar_url") val avatarUrl: String? = null,
    val name: String? = null,
)