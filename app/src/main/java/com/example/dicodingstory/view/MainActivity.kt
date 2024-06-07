package com.example.dicodingstory.view

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingstory.R
import com.example.dicodingstory.adapter.LoadingStateAdapter
import com.example.dicodingstory.adapter.StoryAdapter
import com.example.dicodingstory.databinding.ActivityMainBinding
import com.example.dicodingstory.viewmodel.AuthViewModel
import com.example.dicodingstory.viewmodel.AuthViewModelFactory
import com.example.dicodingstory.viewmodel.MainViewModel
import com.example.dicodingstory.viewmodel.ViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val authViewModel by viewModels<AuthViewModel> {
        AuthViewModelFactory.getInstance(this)
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
                authViewModel.logout()
            }

            R.id.map_button -> {
                val mapsIntent = Intent(this@MainActivity, MapsActivity::class.java)
                startActivity(mapsIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvStory.layoutManager = LinearLayoutManager(this)

        authViewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                getData(user.token)
            }
        }

        binding.postStoryButton.setOnClickListener {
            val postStoryIntent = Intent(this@MainActivity, PostStoryActivity::class.java)
            @Suppress("DEPRECATION")
            startActivityForResult(postStoryIntent, REQUEST_CODE_POST_STORY)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_POST_STORY && resultCode == RESULT_OK) {
            finish()
            startActivity(intent)
        }
    }

    private fun getData(token: String) {
        val adapter = StoryAdapter()
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        val mainViewModel: MainViewModel by viewModels {
            ViewModelFactory(this, token)
        }
        mainViewModel.story.observe(this) {
            adapter.submitData(lifecycle, it)
            binding.rvStory.scrollToPosition(0)
        }
    }

    companion object {
        private const val REQUEST_CODE_POST_STORY = 1
    }
}