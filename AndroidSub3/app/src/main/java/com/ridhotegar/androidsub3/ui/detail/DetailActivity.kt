package com.ridhotegar.androidsub3.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.ridhotegar.androidsub3.R
import com.ridhotegar.androidsub3.adapter.SectionPagerAdapter
import com.ridhotegar.androidsub3.database.FavoriteDao
import com.ridhotegar.androidsub3.database.FavoriteDatabase
import com.ridhotegar.androidsub3.databinding.ActivityDetailBinding
import com.ridhotegar.androidsub3.model.domain.User
import com.ridhotegar.androidsub3.model.domain.UserDetail
import com.ridhotegar.androidsub3.model.response.UserDetailResponse
import com.ridhotegar.androidsub3.network.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class DetailActivity : AppCompatActivity() {

    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding
    private var dataUser: UserDetail? = null

    private lateinit var favoriteDb: FavoriteDatabase
    private lateinit var favoriteDao: FavoriteDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    private var userIsFavorited: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        favoriteDb = FavoriteDatabase.getDatabase(this)
        favoriteDao = favoriteDb.favoriteDao()

        showLoading(false)

        binding?.vpSectionFollow?.adapter = SectionPagerAdapter(this)

        binding?.tblFollow?.let {
            binding?.vpSectionFollow?.let { it1 ->
                TabLayoutMediator(it, it1) { t, i ->
                    t.text = resources.getStringArray(R.array.tabs)[i]
                }.attach()
            }
        }

        val user = intent.getParcelableExtra<User>(KEY_USER)

        if (user != null) {
            getDetailUserByUsername(user.username)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = user?.username

        executorService.execute {
            val id = user?.id

            if (id != null) {
                val userFavorite = favoriteDao.getFavorite(user.id)
                userIsFavorited = userFavorite != null
                invalidateOptionsMenu()
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val favoriteMenu = menu?.findItem(R.id.action_favorite)

        if (userIsFavorited) {
            favoriteMenu?.setIcon(R.drawable.ic_favorite)
        } else {
            favoriteMenu?.setIcon(R.drawable.ic_un_favorite)
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_share -> {
                shareUser(dataUser)
                true
            }
            R.id.action_favorite -> {
                favoriteUser(dataUser)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun favoriteUser(user: UserDetail?) {
        if (user == null) return

        val userFavorite = User(
            id = user.id,
            name = user.name,
            username = user.username,
            photo = user.photo
        )

        if (userIsFavorited) {
            executorService.execute {
                favoriteDao.delete(userFavorite)
            }
        } else {
            executorService.execute {
                favoriteDao.insert(userFavorite)
            }
        }

        userIsFavorited = !userIsFavorited
        invalidateOptionsMenu()
    }

    private fun shareUser(user: UserDetail?) {
        if (user == null) return

        Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_SUBJECT, "Share GitHub User")
            putExtra(Intent.EXTRA_TEXT, "$user")
        }.apply {
            startActivity(Intent.createChooser(this, "Send data"))
        }
    }

    private fun getDetailUserByUsername(username: String?) {
        showLoading(true)

        ApiConfig.userService.getUserDetailByUsername(username)
            .enqueue(object : Callback<UserDetailResponse> {
                override fun onResponse(
                    call: Call<UserDetailResponse>,
                    response: Response<UserDetailResponse>
                ) {
                    showLoading(false)

                    if (response.isSuccessful) {
                        val data = response.body()
                        val bindingView = binding

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

                        dataUser = userDetail

                        if (bindingView != null) {
                            Glide.with(this@DetailActivity)
                                .load(data?.avatarUrl)
                                .circleCrop()
                                .into(bindingView.imgPhoto)

                            bindingView.tvUsername.text = userDetail.username
                            bindingView.tvName.text = userDetail.name
                            bindingView.tvCompany.text = userDetail.company
                            bindingView.tvLocation.text = userDetail.location
                            bindingView.tvFollower.text =
                                "FOLLOWER\n" + userDetail.follower.toString()
                            bindingView.tvFollowing.text =
                                "FOLLOWER\n" + userDetail.following.toString()
                            bindingView.tvRepository.text =
                                "REPOSITORY\n" + userDetail.publicRepos.toString()
                        }

                    } else {
                        Toast.makeText(
                            this@DetailActivity,
                            "Error mendapatkan data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<UserDetailResponse>, t: Throwable) {
                    showLoading(false)
                    Toast.makeText(
                        this@DetailActivity,
                        "Error mendapatkan data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.pbLoading?.isVisible = isLoading
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val KEY_USER = "KEY_USER"
    }

}