package com.ridhotegar.androidsub3.ui.favorite

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.ridhotegar.androidsub3.R
import com.ridhotegar.androidsub3.adapter.ListUserAdapter
import com.ridhotegar.androidsub3.database.FavoriteDao
import com.ridhotegar.androidsub3.database.FavoriteDatabase
import com.ridhotegar.androidsub3.databinding.ActivityFavoriteBinding
import com.ridhotegar.androidsub3.model.domain.User
import com.ridhotegar.androidsub3.ui.detail.DetailActivity

class FavoriteActivity : AppCompatActivity() {

    private var _binding: ActivityFavoriteBinding? = null
    private val binding get() = _binding

    private val listUserAdapter = ListUserAdapter()

    private lateinit var favoriteDb: FavoriteDatabase
    private lateinit var favoriteDao: FavoriteDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        favoriteDb = FavoriteDatabase.getDatabase(this)
        favoriteDao = favoriteDb.favoriteDao()

        showLoading(false)

        showRecyclerList()

        getAllFavorites()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.favorite)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun getAllFavorites() {
        showLoading(true)

        favoriteDao.getAllFavorites().observe(this) {
            showLoading(false)
            listUserAdapter.setData(it)
        }
    }

    private fun showSelectedUser(user: User) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.KEY_USER, user)
        startActivity(intent)
    }

    private fun showRecyclerList() {
        binding?.rvUsers?.layoutManager = LinearLayoutManager(this)
        binding?.rvUsers?.adapter = listUserAdapter.apply {
            setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback {
                override fun onItemClicked(user: User) {
                    showSelectedUser(user)
                }
            })
        }
        binding?.rvUsers?.setHasFixedSize(true)
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.pbLoading?.isVisible = isLoading
    }

}