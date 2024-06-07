package com.example.dicodingstory.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.dicodingstory.R
import com.example.dicodingstory.databinding.ActivityPostStoryBinding
import com.example.dicodingstory.utils.getImageUri
import com.example.dicodingstory.utils.reduceFileImage
import com.example.dicodingstory.utils.uriToFile
import com.example.dicodingstory.view.CameraActivity.Companion.CAMERAX_RESULT
import com.example.dicodingstory.viewmodel.AuthViewModel
import com.example.dicodingstory.viewmodel.StoryViewModel
import com.example.dicodingstory.viewmodel.AuthViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class PostStoryActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var storyViewModel: StoryViewModel
    private lateinit var binding: ActivityPostStoryBinding
    private var currentImageUri: Uri? = null
    private val authViewModel by viewModels<AuthViewModel> {
        AuthViewModelFactory.getInstance(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        supportActionBar?.title = getString(R.string.upload_story)
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
                val mapsIntent = Intent(this@PostStoryActivity, MapsActivity::class.java)
                startActivity(mapsIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.CAMERA, false) &&
                        permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // All permissions granted
                    Toast.makeText(this, getString(R.string.permission_granted), Toast.LENGTH_LONG).show()
                }
                else -> {
                    // Permission denied
                    Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_LONG).show()
                }
            }
        }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        authViewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                binding.uploadButton.setOnClickListener { uploadImage(user.token) }
            }
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.cameraXButton.setOnClickListener { startCameraX() }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            // handle no selected media
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            currentImageUri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun uploadImage(token: String) {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            val description = binding.descEditText.text.toString()
            val isIncludeLocation = binding.checkBox.isChecked
            if (isIncludeLocation) {
                // Get current location
                @SuppressLint("MissingPermission")
                if (allPermissionsGranted()) {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            val latitude = location.latitude.toString()
                            val longitude = location.longitude.toString()
                            uploadToServer(token, imageFile, description, latitude, longitude)
                        } else {
                             showToast(getString(R.string.location_error))
                        }
                    }.addOnFailureListener {
                         showToast("Location error")
                    }
                } else {
                    requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
                }
            } else {
                uploadToServer(token, imageFile, description, null, null)
            }
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun uploadToServer(
        token: String,
        imageFile: File,
        description: String,
        latitude: String?,
        longitude: String?
    ) {
        showLoading(true)

        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )

        val latRequestBody = latitude?.toRequestBody("text/plain".toMediaType())
        val lonRequestBody = longitude?.toRequestBody("text/plain".toMediaType())

        storyViewModel = StoryViewModel()
        storyViewModel.postStory(token, multipartBody, requestBody, latRequestBody, lonRequestBody)
        storyViewModel.isLoading.observe(this) {
            showLoading(it)
        }
        storyViewModel.errorMessage.observe(this) { errorMsg ->
            showToast(errorMsg)
        }
        storyViewModel.successMessage.observe(this) { successMsg ->
            showToast(successMsg)
            setResult(RESULT_OK)
            finish()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }
}