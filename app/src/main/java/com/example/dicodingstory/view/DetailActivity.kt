package com.example.dicodingstory.view

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.dicodingstory.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        postponeEnterTransition()

        val photoUrl = intent.getStringExtra(PHOTO)
        val name = intent.getStringExtra(NAME)
        val description = intent.getStringExtra(DESCRIPTION)
        val transitionNameImage = intent.getStringExtra(TRANSITION_NAME_IMAGE)
        val transitionNameName = intent.getStringExtra(TRANSITION_NAME_NAME)
        val transitionNameDescription = intent.getStringExtra(TRANSITION_NAME_DESCRIPTION)

        binding.storyImageView.transitionName = transitionNameImage
        binding.nameTextView.transitionName = transitionNameName
        binding.descTextView.transitionName = transitionNameDescription

        binding.nameTextView.text = name
        binding.descTextView.text = description

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