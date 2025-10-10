package com.example.lab_week_07

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.lab_week_07.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Initialize the permission launcher
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission granted: Set up the map
                setupMap()
            } else {
                // Permission denied: Show rationale and re-request
                showPermissionRationale {
                    requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
                }
            }
        }

        // 2. Obtain the MapFragment
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // OnMapReady is called when the map is ready to be used.
        // We use a 'when' expression to cleanly check the permission state.
        when {
            // 1. Permission is already granted
            hasLocationPermission() -> {
                setupMap()
            }
            // 2. Permission has been denied once, show rationale before re-requesting
            shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION) -> {
                showPermissionRationale {
                    requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
                }
            }
            // 3. First time requesting or denied with "Don't ask again" (default action is to request)
            else -> {
                requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
            }
        }
    }

    /**
     * Called when location permission is confirmed (either granted previously or just granted).
     * This function enables the location layer and starts fetching the last location.
     */
    private fun setupMap() {
        try {
            // Enable the "My Location" layer (the blue dot) on the map
            mMap.isMyLocationEnabled = true
        } catch (e: SecurityException) {
            // Should not happen if hasLocationPermission() passed, but good for safety
            Log.e("MapsActivity", "Missing location permission required for isMyLocationEnabled", e)
        }

        // Add a default marker (optional, remove this when using real location)
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 10f)) // Added zoom level

        getLastLocation()
    }

    private fun getLastLocation() {
        Log.d("MapsActivity", "getLastLocation() called. Now you would fetch the current location.")
        // TODO: Add logic here to get the last known location from FusedLocationProviderClient
        // and move the camera to that location.
    }

    // This checks if the user already has the permission granted
    private fun hasLocationPermission() =
        ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    // This is used to show a rationale dialog
    private fun showPermissionRationale(positiveAction: () -> Unit) {
        //Create a pop up alert dialog that's used to ask for the required permission again
        AlertDialog.Builder(this)
            .setTitle("Location permission")
            .setMessage("This app will not work without knowing your current location")
            .setPositiveButton(android.R.string.ok) { _, _ -> positiveAction() }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .create().show()
    }
}
