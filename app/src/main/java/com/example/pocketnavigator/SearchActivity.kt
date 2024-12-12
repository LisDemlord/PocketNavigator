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

    // Переменные для карты и слоя местоположения
    private lateinit var mapView: MapView
    private lateinit var myLocationOverlay: MyLocationNewOverlay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Настройка OSMDroid
        Configuration.getInstance().userAgentValue = packageName

        // Инициализация карты
        mapView = findViewById(R.id.map)
        mapView.setMultiTouchControls(true) // Включаем поддержку мультитач
        mapView.controller.setZoom(15.0) // Устанавливаем масштаб карты

        // Запрашиваем разрешения на доступ к местоположению
        requestLocationPermissions()
    }

    // Функция для проверки и запроса разрешений на доступ к местоположению
    private fun requestLocationPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        // Фильтруем отсутствующие разрешения
        val missingPermissions = permissions.filter {
            ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            // Если разрешения отсутствуют, запрашиваем их
            requestPermissionsLauncher.launch(missingPermissions.toTypedArray())
        } else {
            // Если разрешения уже даны, включаем слой местоположения
            setupLocationOverlay()
        }
    }

    // Лаунчер для запроса разрешений
    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.all { it.value }
            if (allGranted) {
                // Если все разрешения даны, включаем слой местоположения
                setupLocationOverlay()
            } else {
                // Если разрешения не даны, показываем сообщение
                Toast.makeText(this, "Необходимо предоставить доступ к местоположению", Toast.LENGTH_SHORT).show()
            }
        }

    // Функция настройки слоя местоположения
    private fun setupLocationOverlay() {
        // Инициализация слоя местоположения
        myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), mapView)
        myLocationOverlay.enableMyLocation() // Включаем отображение местоположения
        myLocationOverlay.enableFollowLocation() // Включаем следование за местоположением

        // Добавляем слой местоположения на карту
        mapView.overlays.add(myLocationOverlay)

        // Устанавливаем центр карты на текущее местоположение после его определения
        myLocationOverlay.runOnFirstFix {
            val myLocation: GeoPoint? = myLocationOverlay.myLocation
            if (myLocation != null) {
                val myPoint = GeoPoint(myLocation.latitude, myLocation.longitude)
                mapView.controller.setCenter(myPoint) // Центрируем карту на местоположении
            }
        }
    }
}
