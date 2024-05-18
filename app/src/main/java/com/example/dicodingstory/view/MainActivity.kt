package com.example.dicodingstory.view

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingstory.R
import com.example.dicodingstory.data.remote.response.ListStoryItem
import com.example.dicodingstory.databinding.ActivityMainBinding
import com.example.dicodingstory.viewmodel.MainViewModel
import com.example.dicodingstory.viewmodel.StoryViewModel
import com.example.dicodingstory.viewmodel.ViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var storyViewModel: StoryViewModel
    private var sessionToken: String? = null
    private val viewModel by viewModels<MainViewModel> {
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
                viewModel.logout()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                sessionToken = user.token
                storyViewModel = StoryViewModel()
                storyViewModel.getStories(user.token)
                storyViewModel.stories.observe(this) { story ->
                    setStoryData(story)
                }
                storyViewModel.isLoading.observe(this) {
                    showLoading(it)
                }
                storyViewModel.errorMessage.observe(this) { errorMsg ->
                    showAlertDialog(errorMsg)
                }
            }
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvStory.addItemDecoration(itemDecoration)

        binding.postStoryButton.setOnClickListener {
            val postStoryIntent = Intent(this@MainActivity, PostStoryActivity::class.java)
            startActivity(postStoryIntent)
        }
    }

    private fun setStoryData(stories: List<ListStoryItem>) {
        val adapter = StoryAdapter()
        adapter.submitList(stories)
        binding.rvStory.adapter = adapter
    }

    private fun showAlertDialog(message: String) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.apply {
            setTitle(getString(R.string.story_retrieval_error))
            setMessage(message)
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}