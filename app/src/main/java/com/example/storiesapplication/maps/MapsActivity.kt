package com.example.storiesapplication.maps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.storiesapplication.R
import com.example.storiesapplication.databinding.ActivityMapsBinding
import com.example.storiesapplication.ui.viewmodel.MainViewModel
import com.example.storiesapplication.ui.viewmodel.SessionViewModel
import com.example.storiesapplication.util.AppPreferences
import com.example.storiesapplication.util.ViewModelFactory
import com.example.storiesapplication.util.dataStore

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var userToken = ""

    private lateinit var mapviewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        val prefs = AppPreferences.getInstance(dataStore)
        val sessionViewModel = ViewModelProvider(this, ViewModelFactory(prefs))[SessionViewModel::class.java]

        sessionViewModel.getUserPreferences().observe(this) { token ->
            userToken = "Bearer $token"
        }

        mapviewModel = MainViewModel(prefs)

        mapviewModel.getStoriesData().observe(this){listStory->
            if (listStory != null) {
                for (story in listStory) {
                    mMap.addMarker(
                        MarkerOptions().position(
                            LatLng(
                                story.lat?.toDouble() ?: 0.0,
                                story.lon?.toDouble() ?: 0.0
                            )
                        ).title(story.name)
                    )?.tag = story
                }
            } else {
                Toast.makeText(this, "No story to show", Toast.LENGTH_SHORT).show()
            }
        }

        mapviewModel.loadStoryLocationData(userToken)
    }
}