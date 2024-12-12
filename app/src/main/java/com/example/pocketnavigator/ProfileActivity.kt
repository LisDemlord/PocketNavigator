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

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Настройка DrawerLayout и Toolbar
        drawerLayout = findViewById(R.id.drawerLayoutProfile)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ).apply {
            drawerArrowDrawable.color = resources.getColor(R.color.white, theme) // Устанавливаем белый цвет
        }
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Настройка NavigationView
        val navigationView: NavigationView = findViewById(R.id.navigationViewProfile)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_map -> {
                    // Возвращение на карту
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                R.id.nav_account -> {
                    drawerLayout.closeDrawers() // Уже находимся на профиле
                }
                R.id.nav_logout -> {
                    logout()
                }
            }
            true
        }

        // Установка email пользователя
        val emailTextView: TextView = findViewById(R.id.emailTextView)
        val user = FirebaseAuth.getInstance().currentUser
        emailTextView.text = user?.email ?: "Неизвестный пользователь"
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }
}
