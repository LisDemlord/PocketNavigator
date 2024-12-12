package com.example.pocketnavigator

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MainActivity : AppCompatActivity() {

    // Объявление переменных для карты, меню и слоя местоположения
    private lateinit var map: MapView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var myLocationOverlay: MyLocationNewOverlay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Настройка OSMDroid (задание конфигурации)
        Configuration.getInstance().userAgentValue = packageName
        setContentView(R.layout.activity_main)

        // Инициализация Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Настройка бокового меню (DrawerLayout) и кнопки "гамбургер"
        drawerLayout = findViewById(R.id.drawerLayout)
        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ).apply {
            drawerArrowDrawable.color = resources.getColor(R.color.white, theme) // Устанавливаем белый цвет иконки
        }
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Настройка NavigationView для обработки нажатий на элементы меню
        val navigationView: NavigationView = findViewById(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_map -> {
                    // Обработка нажатия на пункт меню "Карта"
                    Toast.makeText(this, "Карта", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_account -> {
                    // Переход на экран профиля
                    startActivity(Intent(this, ProfileActivity::class.java))
                }
                R.id.nav_logout -> {
                    // Выход из аккаунта
                    logout()
                }
            }
            drawerLayout.closeDrawers() // Закрываем меню после выбора пункта
            true
        }

        // Инициализация карты
        map = findViewById(R.id.map)
        map.setMultiTouchControls(true) // Включение управления жестами
        map.controller.setZoom(15.0) // Установка начального масштаба

        // Запрос разрешений для доступа к местоположению
        requestLocationPermissions()
    }

    // Функция для запроса разрешений на доступ к местоположению
    private fun requestLocationPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION, // Точное местоположение
            Manifest.permission.ACCESS_COARSE_LOCATION // Приблизительное местоположение
        )

        // Проверяем, какие разрешения отсутствуют
        val missingPermissions = permissions.filter {
            ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        // Если разрешения не предоставлены, запрашиваем их
        if (missingPermissions.isNotEmpty()) {
            requestPermissionsLauncher.launch(missingPermissions.toTypedArray())
        } else {
            // Если разрешения уже предоставлены, настраиваем слой местоположения
            setupLocationOverlay()
        }
    }

    // Лаунчер для обработки результата запроса разрешений
    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.all { it.value } // Проверяем, все ли разрешения предоставлены
            if (allGranted) {
                setupLocationOverlay() // Настраиваем слой местоположения
            } else {
                // Если разрешения не предоставлены, показываем сообщение
                Toast.makeText(this, "Необходимо предоставить доступ к местоположению", Toast.LENGTH_SHORT).show()
            }
        }

    // Настройка слоя местоположения
    private fun setupLocationOverlay() {
        myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), map)
        myLocationOverlay.enableMyLocation() // Включаем отображение местоположения
        myLocationOverlay.enableFollowLocation() // Включаем следование за местоположением

        map.overlays.add(myLocationOverlay) // Добавляем слой местоположения на карту

        // При первом определении местоположения центрируем карту
        myLocationOverlay.runOnFirstFix {
            val myLocation = myLocationOverlay.myLocation
            if (myLocation != null) {
                val myPoint = GeoPoint(myLocation.latitude, myLocation.longitude)
                map.controller.setCenter(myPoint)
            }
        }
    }

    // Обработка жизненного цикла карты
    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        map.onDetach()
    }

    // Функция для выхода из аккаунта
    private fun logout() {
        FirebaseAuth.getInstance().signOut() // Выходим из Firebase аккаунта
        Toast.makeText(this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, AuthActivity::class.java)) // Переходим на экран авторизации
        finish() // Завершаем текущую активность
    }
}
