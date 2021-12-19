package com.ridhotegar.androidsub3.model.domain

data class UserDetail(
    val id: Int,
    val name: String,
    val username: String,
    val photo: String,
    val location: String,
    val company: String,
    val publicRepos: Int,
    val follower: Int,
    val following: Int
)