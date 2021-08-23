package dev.decagon.godday.locationclass

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class LocationTrackingActivity : AppCompatActivity(), OnMapReadyCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_tracking)

        title = "Tracking My Location"

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    // Initialize the map
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
    }

    companion object {
        private lateinit var map: GoogleMap

//         Method to update the map from the MainActivity
        fun updateMap(latitude: Double, longitude: Double)  {
            if (this::map.isInitialized) {
                val myLatLng = LatLng(latitude, longitude)
                val update = CameraUpdateFactory.newLatLngZoom(myLatLng, 16.0f)
                map.clear()
                map.addMarker(
                    MarkerOptions().position(myLatLng).title("Here's my current location")
                )
                map.moveCamera(update)
            }
        }
    }
}