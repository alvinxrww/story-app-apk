package com.example.dicodingstory.view

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.dicodingstory.R
import com.example.dicodingstory.databinding.ActivityDetailBinding
import com.example.dicodingstory.viewmodel.MainViewModel
import com.example.dicodingstory.viewmodel.ViewModelFactory

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.language_button -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
            R.id.logout_button -> {
                mainViewModel.logout()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainViewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

        postponeEnterTransition()

        val photoUrl = intent.getStringExtra(PHOTO)
        val name = intent.getStringExtra(NAME)
        val description = intent.getStringExtra(DESCRIPTION)
        val transitionNameImage = intent.getStringExtra(TRANSITION_NAME_IMAGE)
        val transitionNameName = intent.getStringExtra(TRANSITION_NAME_NAME)
        val transitionNameDescription = intent.getStringExtra(TRANSITION_NAME_DESCRIPTION)

        binding.apply {
            storyImageView.transitionName = transitionNameImage
            nameTextView.transitionName = transitionNameName
            descTextView.transitionName = transitionNameDescription

            nameTextView.text = name
            descTextView.text = description
        }

        Glide.with(this)
            .load(photoUrl)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    startPostponedEnterTransition()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    startPostponedEnterTransition()
                    return false
                }
            })
            .into(binding.storyImageView)
    }

    companion object {
        const val PHOTO = "photoUrl"
        const val NAME = "name"
        const val DESCRIPTION = "description"

        const val TRANSITION_NAME_IMAGE = "transition_name_image"
        const val TRANSITION_NAME_NAME = "transition_name_name"
        const val TRANSITION_NAME_DESCRIPTION = "transition_name_description"
    }
}