package com.example.pocketnavigator

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Задержка экрана загрузки (3 секунды)
        Handler(Looper.getMainLooper()).postDelayed({
            // Проверка авторизации
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                // Если пользователь авторизован, переходим на карту
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // Если пользователь не авторизован, переходим на регистрацию/авторизацию
                startActivity(Intent(this, AuthActivity::class.java))
            }
            finish()
        }, 3000)
    }
}
