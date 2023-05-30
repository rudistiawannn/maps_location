package com.example.storiesapplication.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.storiesapplication.R
import com.example.storiesapplication.adapter.StoryListAdapter
import com.example.storiesapplication.databinding.ActivityMainBinding
import com.example.storiesapplication.maps.MapsActivity
import com.example.storiesapplication.response.Story
import com.example.storiesapplication.service.APIService
import com.example.storiesapplication.service.getAPIService
import com.example.storiesapplication.ui.viewmodel.MainViewModel
import com.example.storiesapplication.ui.viewmodel.StoryPagerViewModel
import com.example.storiesapplication.ui.viewmodel.ViewModelStoryFactory
import com.example.storiesapplication.util.AppPreferences
import com.example.storiesapplication.util.Constants
import com.example.storiesapplication.util.ViewModelFactory

class MainActivity : AppCompatActivity() {
    companion object {
        const val REQ_PERMISSION_CODE = 1111
        const val INTENT_CREATE_STORY = 2222
    }

    private val Context.dataStore by preferencesDataStore(name = Constants.PREFS_NAME)

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private val storyViewModel: StoryPagerViewModel by viewModels {
        ViewModelStoryFactory(
            this,
            getAPIService(),
            token
        )
    }
    private val storyListAdapter = StoryListAdapter()
    private lateinit var recyclerView: RecyclerView
    private var token = ""

    private val intentCreateStoryResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == INTENT_CREATE_STORY) {
                storyViewModel.story
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!permissionAllowed(Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQ_PERMISSION_CODE
            )
        }

        val appPrefs = AppPreferences.getInstance(dataStore)
        mainViewModel =
            ViewModelProvider(
                this@MainActivity,
                ViewModelFactory(appPrefs)
            )[MainViewModel::class.java]

        setRecyclerView()

        binding.mainFabCreate.setOnClickListener {
            if (!permissionAllowed(Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    REQ_PERMISSION_CODE
                )
            } else {
                intentCreateStoryResult.launch(Intent(this, AddStoryActivity::class.java))
            }
        }

        storyViewModel.story.observe(this){ storiesData ->
            updateRecyclerViewData(storiesData)
        }

        mainViewModel.getStoriesError().observe(this) { storiesError ->
            storiesError.getData()?.let {
                Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_main_logout) {
            mainViewModel.logout()

            Toast.makeText(
                this@MainActivity,
                getString(R.string.logout_success),
                Toast.LENGTH_SHORT
            ).show()
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
        }
        when (item.itemId) {
            R.id.menu_maps -> {
                startActivity(Intent(this, MapsActivity::class.java))
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQ_PERMISSION_CODE) {
            if (permissionAllowed(Manifest.permission.CAMERA)) {
                Toast.makeText(this, getString(R.string.camera_allowed), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, getString(R.string.camera_not_allowed), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun permissionAllowed(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun setRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)

        recyclerView = binding.mainRvStories
        recyclerView.apply {
            adapter = storyListAdapter
            layoutManager = linearLayoutManager
        }
    }

    private fun updateRecyclerViewData(stories: PagingData<Story>) {
        storyListAdapter.submitData(lifecycle, stories)
    }
}