package com.example.pocketnavigator

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class SearchActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var myLocationOverlay: MyLocationNewOverlay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Настройка OSMDroid
        Configuration.getInstance().userAgentValue = packageName

        mapView = findViewById(R.id.map)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(15.0)

        requestLocationPermissions()
    }

    private fun requestLocationPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val missingPermissions = permissions.filter {
            ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            // Запрос разрешений
            requestPermissionsLauncher.launch(missingPermissions.toTypedArray())
        } else {
            // Если разрешения уже даны, включаем слой местоположения
            setupLocationOverlay()
        }
    }

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.all { it.value }
            if (allGranted) {
                setupLocationOverlay()
            } else {
                Toast.makeText(this, "Необходимо предоставить доступ к местоположению", Toast.LENGTH_SHORT).show()
            }
        }

    private fun setupLocationOverlay() {
        myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), mapView)
        myLocationOverlay.enableMyLocation()
        myLocationOverlay.enableFollowLocation()

        mapView.overlays.add(myLocationOverlay)

        myLocationOverlay.runOnFirstFix {
            val myLocation: GeoPoint? = myLocationOverlay.myLocation
            if (myLocation != null) {
                val myPoint = GeoPoint(myLocation.latitude, myLocation.longitude)
                mapView.controller.setCenter(myPoint)
            }
        }
    }
}
