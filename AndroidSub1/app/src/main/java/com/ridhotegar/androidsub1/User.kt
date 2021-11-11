package com.ridhotegar.androidsub1

import android.location.Location
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User (
    var name: String?,
    var username: String?,
    var photo: Int?,
    var location: String?,
    var repository: String?,
    var company: String?,
    var follower: String?,
    var following: String?
    ) : Parcelable
