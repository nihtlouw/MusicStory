package com.dicoding.musicstory.ui.createstory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils.isEmpty
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.dicoding.musicstory.R
import com.dicoding.musicstory.customview.CustNotif
import com.dicoding.musicstory.data.Result
import com.dicoding.musicstory.databinding.ActivityCreateStoryBinding
import com.dicoding.musicstory.ui.mainmenu.MainAct
import com.dicoding.musicstory.utils.FactoryVM
import com.dicoding.musicstory.utils.createCustomTempFile
import com.dicoding.musicstory.utils.reduceFileImage
import com.dicoding.musicstory.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class CreateStoryAct : AppCompatActivity() {
    private lateinit var binding: ActivityCreateStoryBinding
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    private lateinit var factory: FactoryVM
    private val createStoryViewModel: CreateStoryVM by viewModels { factory }
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double? = null
    private var longitude: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupViewModel()
        setupToolbar()
        getMyLastLocation()
        buttonGalleryHandler()
        buttonCameraHandler()
        buttonSubmitStoryHandler()
    }

    private fun setupViewModel() {
        factory = FactoryVM.getInstance(binding.root.context)
    }

    private fun setupToolbar() {
        title = resources.getString(R.string.create_story)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.CAMERA] ?: false -> {}
                permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false -> {}
                else -> {
                    Toast.makeText(this@CreateStoryAct, R.string.permission_denied, Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION) &&
            checkPermission(Manifest.permission.CAMERA) &&
            checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
                } else {
                    Toast.makeText(
                        this@CreateStoryAct,
                        getString(R.string.location_not_found),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
            )
        }
    }

    private fun buttonGalleryHandler() {
        binding.createStoryLayout.galleryButton.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            val chooser = Intent.createChooser(intent, "Choose a Picture")
            launcherIntentGallery.launch(chooser)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile
            val result = BitmapFactory.decodeFile(myFile.path)

            binding.createStoryLayout.imagePickerView.setImageBitmap(result)
        }
    }

    private fun buttonCameraHandler() {
        binding.createStoryLayout.cameraButton.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.resolveActivity(packageManager)

            createCustomTempFile(applicationContext).also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this@CreateStoryAct,
                    "com.dicoding.musicstory.mycamera",
                    it
                )
                currentPhotoPath = it.absolutePath
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                launcherIntentCamera.launch(intent)
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@CreateStoryAct)
            getFile = myFile
            binding.createStoryLayout.imagePickerView.setImageURI(selectedImg)
        }
    }

    private fun buttonSubmitStoryHandler() {
        binding.createStoryLayout.submitStoryButton.setOnClickListener {
            val description = binding.createStoryLayout.descriptionEditText.text.toString()
            if (!isEmpty(description) && getFile != null && latitude != null && longitude != null) {
                createStory(description)
            } else {
                CustNotif(
                    this,
                    R.string.error_validation,
                    R.drawable.error_form
                ).show()
            }
        }
    }

    private fun convertImage(): MultipartBody.Part {
        val file = reduceFileImage(getFile as File)
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())

        return MultipartBody.Part.createFormData(
            "photo",
            file.name,
            requestImageFile
        )
    }

    private fun convertDescription(description: String): RequestBody {
        return description.toRequestBody("text/plain".toMediaType())
    }

    private fun createStory(description: String) {
        val image = convertImage()
        val desc = convertDescription(description)
        createStoryViewModel.postCreateStory(
            image,
            desc,
            latitude!!,
            longitude!!
        ).observe(this@CreateStoryAct) { result ->
            if (result != null) {
                when(result) {
                    is Result.Loading -> {
                        loadingHandler(true)
                    }
                    is Result.Error -> {
                        loadingHandler(false)
                        errorHandler()
                    }
                    is Result.Success -> {
                        successHandler()
                    }
                }
            }
        }
    }

    private fun loadingHandler(isLoading: Boolean) {
        if (isLoading) {
            binding.loadingLayout.root.visibility = View.VISIBLE
            binding.createStoryLayout.root.visibility = View.GONE
        } else {
            binding.loadingLayout.root.visibility = View.GONE
            binding.createStoryLayout.root.visibility = View.VISIBLE
        }
    }

    private fun errorHandler() {
        CustNotif(this, R.string.error_message, R.drawable.error).show()
    }

    private fun successHandler() {
        CustNotif(
            this,
            R.string.success_create_story,
            R.drawable.story_created,
            fun() {
                val moveActivity = Intent(this@CreateStoryAct, MainAct::class.java)
                moveActivity.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(moveActivity)
                finish()
            }
        ).show()
        binding.createStoryLayout.imagePickerView.setImageResource(R.drawable.image_picker)
        binding.createStoryLayout.descriptionEditText.text?.clear()
    }
}