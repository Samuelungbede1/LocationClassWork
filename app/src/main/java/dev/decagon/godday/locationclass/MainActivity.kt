package dev.decagon.godday.locationclass

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    /**
     *                          INTRO TO LOCATION API
     *
     *  Your app can access the set of supported location services through classes
     *  in the com.google.android.gms.location package.
     *  Look at the main classes:
     *
     * *    FusedLocationProviderClient:
     *          This is the central component of the location framework.
     *          Once created, you use it to request location updates and get the last known location.
     *
     * *   LocationRequest:
     *          This is a data object that contains quality-of-service parameters
     *          for requests (intervals for updates, priorities, and accuracy).
     *          This is passed to the FusedLocationProviderClient when you request location updates.
     *
     * *   LocationCallback:
     *          This is used for receiving notifications when the device location has changed
     *          or can no longer be determined.
     *          This is passed a LocationResult where you can get the Location for your use cases.
     */

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }

    // Declare Views
    private lateinit var myCoOrdinates: TextView
    private lateinit var showCoOrdsBtn: Button
    private lateinit var gotoMapBtn: Button

    // Declare location utils
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationRequest: LocationRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    // Initialize Views
    private fun initViews() {
        myCoOrdinates = findViewById(R.id.my_coordinates)
        showCoOrdsBtn = findViewById(R.id.show_coords)
        gotoMapBtn = findViewById(R.id.go_to_map)

        myCoOrdinates.text = getString(R.string.my_coords, "", "")
        showCoOrdsBtn.setOnClickListener { getCurrentLocation() }
        gotoMapBtn.setOnClickListener {
            startActivity(Intent(this, LocationTrackingActivity::class.java))
        }
    }

    // Method that checks if permission to access fine location has been granted
    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // Method use to request for permission to access fine location
    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_LOCATION_PERMISSION)
    }

    /**
     * Checks the action of the user regarding the permission request
     * If the permission was granted, get the user's current location
     * else show snackbar
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                getCurrentLocation()
            } else {
                Snackbar.make(myCoOrdinates, "Permission denied!", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    /**
     * This method gets the user's exact location every 10secs and the UI.
     * If no such user's location is found,
     * the method logs an error message to the logcat.
     */
    @SuppressLint("MissingPermission", "StringFormatMatches")
    private fun getCurrentLocation() {
        if (!isPermissionGranted()) {
            requestLocationPermissions()
        } else {
            if (locationRequest == null) {
                locationRequest = LocationRequest.create().apply {
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    interval = 5_000
                    fastestInterval = 2_000

                    val locationCallback = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            getCurrentLocation()
                        }
                    }

                    // Request location update
                    fusedLocationClient.requestLocationUpdates(this, locationCallback, mainLooper)
                }
            }

            // Update the UI when the last location is available
            fusedLocationClient.lastLocation.addOnCompleteListener {
                val myLocation = it.result

                if (myLocation != null) {
                    myCoOrdinates.text =
                        getString(R.string.my_coords, "${myLocation.latitude}", "${myLocation.longitude}")
                    LocationTrackingActivity.updateMap(myLocation.latitude, myLocation.longitude)
                } else {
                    Log.d("Location", "No location found!")
                }
            }
        }
    }
}