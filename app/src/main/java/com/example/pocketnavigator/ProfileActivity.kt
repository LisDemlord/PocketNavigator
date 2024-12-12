package com.example.pocketnavigator

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    // Переменные для бокового меню и его кнопки
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Инициализация DrawerLayout и Toolbar
        drawerLayout = findViewById(R.id.drawerLayoutProfile)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar) // Устанавливаем Toolbar в качестве ActionBar

        // Настройка кнопки "гамбургер" для бокового меню
        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ).apply {
            // Устанавливаем белый цвет для иконки
            drawerArrowDrawable.color = resources.getColor(R.color.white, theme)
        }
        drawerLayout.addDrawerListener(toggle) // Добавляем слушатель
        toggle.syncState() // Синхронизируем состояние

        // Настройка бокового меню (NavigationView)
        val navigationView: NavigationView = findViewById(R.id.navigationViewProfile)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_map -> {
                    // Переход на экран карты
                    startActivity(Intent(this, MainActivity::class.java))
                    finish() // Завершаем текущую активность
                }
                R.id.nav_account -> {
                    // Уже находимся на экране профиля, просто закрываем меню
                    drawerLayout.closeDrawers()
                }
                R.id.nav_logout -> {
                    // Выход из аккаунта
                    logout()
                }
            }
            true
        }

        // Установка email текущего пользователя
        val emailTextView: TextView = findViewById(R.id.emailTextView)
        val user = FirebaseAuth.getInstance().currentUser // Получаем текущего пользователя
        emailTextView.text = user?.email ?: "Неизвестный пользователь" // Отображаем email или сообщение по умолчанию
    }

    // Функция для выхода из аккаунта
    private fun logout() {
        FirebaseAuth.getInstance().signOut() // Выходим из Firebase аккаунта
        startActivity(Intent(this, AuthActivity::class.java)) // Переходим на экран авторизации
        finish() // Завершаем текущую активность
    }
}
