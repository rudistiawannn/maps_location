package com.example.storiesapplication.ui.activity

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.storiesapplication.BuildConfig
import com.example.storiesapplication.R
import com.example.storiesapplication.databinding.ActivityAddStoryBinding
import com.example.storiesapplication.response.CreateStoryResponse
import com.example.storiesapplication.ui.viewmodel.CreateStoryViewModel
import com.example.storiesapplication.ui.viewmodel.OnSuccessCallback
import com.example.storiesapplication.util.AppPreferences
import com.example.storiesapplication.util.Constants
import com.example.storiesapplication.util.ViewModelFactory
import java.io.*

class AddStoryActivity : AppCompatActivity() {
    private val Context.dataStore by preferencesDataStore(name = Constants.PREFS_NAME)
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var createStoryViewModel: CreateStoryViewModel

    private var currentFileImg: File? = null
    private lateinit var tempCameraImg: String

    private val intentGalleryResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val resultImg = getFileUri(this, it.data?.data as Uri)
                currentFileImg = resultImg

                Glide.with(this)
                    .load(resultImg)
                    .placeholder(R.drawable.baseline_image_24)
                    .error(R.drawable.baseline_image_24)
                    .into(binding.ivPreview)
            }
        }

    private val intentCameraResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val resultImg = File(tempCameraImg)
                currentFileImg = resultImg

                Glide.with(this)
                    .load(resultImg)
                    .placeholder(R.drawable.baseline_image_24)
                    .error(R.drawable.baseline_image_24)
                    .into(binding.ivPreview)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        val appPrefs = AppPreferences.getInstance(dataStore)
        createStoryViewModel =
            ViewModelProvider(
                this,
                ViewModelFactory(appPrefs)
            )[CreateStoryViewModel::class.java]

        binding.btnCamera.setOnClickListener {
            val tmpFile = createImageTempFile(this)

            tempCameraImg = tmpFile.absolutePath
            val tempFileUri: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                BuildConfig.APPLICATION_ID,
                tmpFile
            )

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                resolveActivity(packageManager)
                putExtra(MediaStore.EXTRA_OUTPUT, tempFileUri)
            }

            intentCameraResult.launch(intent)
        }

        binding.btnGallery.setOnClickListener {
            intentGalleryResult.launch(
                Intent.createChooser(
                    Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" },
                    getString(R.string.choose_picture)
                )
            )
        }

        binding.btnSubmit.setOnClickListener {
            if (currentFileImg != null && !TextUtils.isEmpty(binding.etDescription.text.toString())) {
                val compressed = compressImageFile(currentFileImg!!)

                createStoryViewModel.submit(
                    object : OnSuccessCallback<CreateStoryResponse> {
                        override fun onSuccess(message: CreateStoryResponse) {
                            Toast.makeText(
                                this@AddStoryActivity,
                                message.message,
                                Toast.LENGTH_SHORT
                            ).show()

                            setResult(MainActivity.INTENT_CREATE_STORY)
                            finish()
                        }
                    },
                    binding.etDescription.text.toString(),
                    compressed,
                    null,
                    null
                )
            } else {
                Toast.makeText(this, "Add the image and description", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        createStoryViewModel.isLoading().observe(this) { isLoading ->
            binding.apply {
                progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                btnCamera.isEnabled = !isLoading
                btnGallery.isEnabled = !isLoading
                etDescription.isEnabled = !isLoading
                btnSubmit.isEnabled = !isLoading
            }
        }

        createStoryViewModel.getCreateError().observe(this) { createError ->
            createError.getData()?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun compressImageFile(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)

        var compressQuality = 100
        var streamLength: Int

        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)

            val bmpPicByteArray = bmpStream.toByteArray()

            streamLength = bmpPicByteArray.size
            compressQuality -= 10
        } while (streamLength > 1000000)

        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    private fun createImageTempFile(context: Context): File {
        return File.createTempFile(
            System.currentTimeMillis().toString() + "_" + (Math.random() * 1000).toInt().toString(),
            ".jpg",
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )
    }

    private fun getFileUri(context: Context, uriToFile: Uri): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myFile = createImageTempFile(context)

        val inputStream = contentResolver.openInputStream(uriToFile) as InputStream
        val outputStream: OutputStream = FileOutputStream(myFile)

        val buffer = ByteArray(1024)
        var length: Int

        while (inputStream.read(buffer).also { length = it } > 0) {
            outputStream.write(buffer, 0, length)
        }

        inputStream.close()
        outputStream.close()

        return myFile
    }
}