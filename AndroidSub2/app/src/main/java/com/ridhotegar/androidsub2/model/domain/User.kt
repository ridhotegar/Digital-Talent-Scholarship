package com.ridhotegar.androidsub2.model.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: Int,
    val name: String,
    val username: String,
    val photo: String,
) : Parcelable
