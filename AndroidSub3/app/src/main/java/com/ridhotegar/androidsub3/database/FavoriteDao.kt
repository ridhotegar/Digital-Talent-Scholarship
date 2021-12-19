package com.ridhotegar.androidsub3.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ridhotegar.androidsub3.model.domain.User

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(user: User)

    @Delete
    fun delete(user: User)

    @Query("SELECT * from user ORDER BY id ASC")
    fun getAllFavorites(): LiveData<List<User>>

    @Query("SELECT * from user WHERE id = :id")
    fun getFavorite(id: Int): User?
}