package com.example.dicodingstory.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.dicodingstory.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val photo = intent.getStringExtra(PHOTO)
        val name = intent.getStringExtra(NAME)
        val description = intent.getStringExtra(DESCRIPTION)

        setupData(photo, name, description)
    }

    private fun setupData(photo: String?, name: String?, description: String?) {
        Glide.with(this)
            .load(photo)
            .into(binding.storyImageView)
        binding.nameTextView.text = name
        binding.descTextView.text = description
    }

    companion object {
        const val PHOTO = "photoUrl"
        const val NAME = "name"
        const val DESCRIPTION = "description"
    }
}