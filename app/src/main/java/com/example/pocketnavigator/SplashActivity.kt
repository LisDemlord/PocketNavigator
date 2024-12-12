package com.example.pocketnavigator

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("CustomSplashScreen") // Подавляем предупреждение о кастомном экране загрузки
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Задержка экрана загрузки на 3 секунды
        Handler(Looper.getMainLooper()).postDelayed({
            // Проверяем, авторизован ли пользователь
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                // Если пользователь авторизован, открываем MainActivity (экран с картой)
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // Если пользователь не авторизован, переходим на экран авторизации
                startActivity(Intent(this, AuthActivity::class.java))
            }
            // Завершаем текущую активность, чтобы пользователь не мог вернуться на экран загрузки
            finish()
        }, 3000) // Задержка в миллисекундах (3000 = 3 секунды)
    }
}
