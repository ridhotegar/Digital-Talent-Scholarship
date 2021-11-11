package com.ridhotegar.androidsub1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class DetailActivity : AppCompatActivity() {
    private var imgPhoto: ImageView? = null
    private var tvName: TextView? = null
    private var tvUsername: TextView? = null
    private var tvCompany: TextView? = null
    private var tvLocation: TextView? = null
    private var tvRepository: TextView? = null
    private var tvFollower: TextView? = null
    private var tvFollowing: TextView? = null
    private var dataUser: User? = null

    companion object {
        const val KEY_USER = "KEY_USER"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        var imgPhoto: ImageView = findViewById(R.id.img_item_photo)
        var tvName: TextView = findViewById(R.id.tv_item_name)
        var tvUsername: TextView = findViewById(R.id.tv_item_username)
        var tvCompany: TextView = findViewById(R.id.tv_item_company)
        var tvLocation: TextView = findViewById(R.id.tv_item_location)
        var tvRepository: TextView = findViewById(R.id.tv_item_repository)
        var tvFollowing: TextView = findViewById(R.id.tv_item_following)
        var tvFollower: TextView = findViewById(R.id.tv_item_follower)

        dataUser = intent.getParcelableExtra(KEY_USER) as? User

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = dataUser?.username

        imgPhoto?.let { Glide.with(this).load(dataUser?.photo).circleCrop().into(it) }
        tvUsername?.text = dataUser?.username
        tvName?.text = dataUser?.name
        tvCompany?.text = dataUser?.company
        tvLocation?.text = dataUser?.location
        tvRepository?.text = "REPOSITORY\n"+dataUser?.repository
        tvFollower?.text = "FOLLOWER\n"+dataUser?.follower
        tvFollowing?.text = "FOLLOWING\n"+dataUser?.following
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
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun shareUser(user: User?){
        Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_SUBJECT, "Share GitHub User")
            putExtra(Intent.EXTRA_TEXT, "$user")
        }.apply {
            startActivity(Intent.createChooser(this, "Send data"))
        }
    }
}