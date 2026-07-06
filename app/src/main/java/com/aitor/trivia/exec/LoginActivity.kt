package com.aitor.trivia.exec

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aitor.trivia.R
import com.aitor.trivia.dao.ApiClient
import com.aitor.trivia.util.ThemeManager
import com.aitor.trivia.util.UserSession

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.apply(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etUser   = findViewById<EditText>(R.id.etUser)
        val etPass   = findViewById<EditText>(R.id.etPass)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnReg   = findViewById<TextView>(R.id.btnRegister)
        val progress = findViewById<ProgressBar>(R.id.progressLogin)

        btnLogin.setOnClickListener {
            val user = etUser.text.toString().trim()
            val pass = etPass.text.toString()

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnLogin.isEnabled = false
            progress.visibility = View.VISIBLE

            Thread {
                val result = ApiClient.login(user, pass)
                runOnUiThread {
                    progress.visibility = View.GONE
                    btnLogin.isEnabled = true
                    if (result.success) {
                        UserSession.save(this, user, result.idUser)
                        startActivity(
                            Intent(this, MenuActivity::class.java)
                                .putExtra("USERNAME", user)
                                .putExtra("ID_USER", result.idUser)
                        )
                        finish()
                    } else {
                        Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    }
                }
            }.start()
        }

        btnReg.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }
    }
}
