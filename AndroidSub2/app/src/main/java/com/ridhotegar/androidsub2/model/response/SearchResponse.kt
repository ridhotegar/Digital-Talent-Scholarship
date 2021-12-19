package com.ridhotegar.androidsub2.model.response

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("items") val items: List<UserItemResponse>? = null
)