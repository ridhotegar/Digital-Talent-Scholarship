package com.ridhotegar.androidsub3.ui.main

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ridhotegar.androidsub3.R
import com.ridhotegar.androidsub3.SettingPreferences
import com.ridhotegar.androidsub3.adapter.ListUserAdapter
import com.ridhotegar.androidsub3.databinding.ActivityMainBinding
import com.ridhotegar.androidsub3.model.domain.User
import com.ridhotegar.androidsub3.model.domain.UserDetail
import com.ridhotegar.androidsub3.model.response.SearchResponse
import com.ridhotegar.androidsub3.model.response.UserDetailResponse
import com.ridhotegar.androidsub3.network.ApiConfig
import com.ridhotegar.androidsub3.ui.detail.DetailActivity
import com.ridhotegar.androidsub3.ui.detail.DetailActivity.Companion.KEY_USER
import com.ridhotegar.androidsub3.ui.favorite.FavoriteActivity
import com.ridhotegar.androidsub3.ui.theme.ThemeActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding

    private val listUserAdapter = ListUserAdapter()

    private val listUserFlow = MutableStateFlow<User?>(null)
    private val listUser = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        val pref = SettingPreferences.getInstance(dataStore)

        lifecycleScope.launchWhenCreated {
            pref.getThemeSetting().collect { isDarkModeActive: Boolean ->
                if (isDarkModeActive) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }

        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        showLoading(false)

        showRecyclerList()

        lifecycleScope.launchWhenCreated {
            listUserFlow.collect {
                if (it != null) {
                    listUser.add(it)
                    listUserAdapter.setData(listUser)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        setUpSearch(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.theme -> {
                val intent = Intent(this, ThemeActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.favorite -> {
                val intent = Intent(this, FavoriteActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSelectedUser(user: User) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(KEY_USER, user)
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

    private fun setUpSearch(menu: Menu) {
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.search_hint)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isNotEmpty()) {
                    findUsers(username = query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }

    private fun findUsers(username: String?) {
        showLoading(true)

        ApiConfig.userService.findUsers(username).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
            ) {
                showLoading(false)

                if (response.isSuccessful) {
                    val users = response.body()?.items ?: emptyList()
                    if (users.isNullOrEmpty()) {
                        Toast.makeText(
                            this@MainActivity,
                            "Pengguna tidak ditemukan",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        for (user in users) {
                            getDetailUserByUsername(user.username)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                showLoading(false)
                Toast.makeText(this@MainActivity, "Error mendapatkan data", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun getDetailUserByUsername(username: String?) {
        ApiConfig.userService.getUserDetailByUsername(username)
            .enqueue(object : Callback<UserDetailResponse> {
                override fun onResponse(
                    call: Call<UserDetailResponse>,
                    response: Response<UserDetailResponse>
                ) {
                    if (response.isSuccessful) {
                        val data = response.body()

                        val userDetail = UserDetail(
                            id = data?.id ?: -1,
                            name = data?.name ?: "-",
                            username = data?.username ?: "-",
                            photo = data?.avatarUrl ?: "-",
                            location = data?.location ?: "-",
                            company = data?.company ?: "-",
                            publicRepos = data?.publicRepos ?: 0,
                            follower = data?.follower ?: 0,
                            following = data?.following ?: 0,
                        )

                        val user = User(
                            id = userDetail.id,
                            name = userDetail.name,
                            username = userDetail.username,
                            photo = userDetail.photo
                        )

                        listUserFlow.value = user
                    }
                }

                override fun onFailure(call: Call<UserDetailResponse>, t: Throwable) {

                }
            })
    }

}