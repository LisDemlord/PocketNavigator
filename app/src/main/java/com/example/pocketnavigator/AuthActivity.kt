package com.example.pocketnavigator

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {

    // Переменная для работы с Firebase Authentication
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // Инициализация FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Найдём элементы ввода и кнопки на экране авторизации
        val emailField: EditText = findViewById(R.id.emailField) // Поле ввода email
        val passwordField: EditText = findViewById(R.id.passwordField) // Поле ввода пароля
        val loginButton: Button = findViewById(R.id.loginButton) // Кнопка входа
        val registerButton: Button = findViewById(R.id.registerButton) // Кнопка регистрации

        // Логика для кнопки "Войти"
        loginButton.setOnClickListener {
            val email = emailField.text.toString() // Получаем текст из поля email
            val password = passwordField.text.toString() // Получаем текст из поля пароля

            // Попытка авторизации через Firebase
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Если авторизация успешна, переходим на главную активность
                        startActivity(Intent(this, MainActivity::class.java))
                        finish() // Закрываем активность авторизации
                    } else {
                        // Если авторизация не удалась, показываем сообщение об ошибке
                        Toast.makeText(this, "Ошибка входа: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Логика для кнопки "Регистрация"
        registerButton.setOnClickListener {
            val email = emailField.text.toString() // Получаем текст из поля email
            val password = passwordField.text.toString() // Получаем текст из поля пароля

            // Проверяем, что поля не пустые
            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Попытка регистрации через Firebase
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Если регистрация успешна, показываем сообщение об успехе
                            Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_SHORT).show()
                        } else {
                            // Если произошла ошибка при регистрации, показываем сообщение об ошибке
                            val errorMessage = task.exception?.message ?: "Неизвестная ошибка"
                            Toast.makeText(this, "Ошибка регистрации: $errorMessage", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                // Если поля пустые, показываем предупреждение
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }
    }
}