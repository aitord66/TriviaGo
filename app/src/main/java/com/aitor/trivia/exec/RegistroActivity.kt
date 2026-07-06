package com.aitor.trivia.exec

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aitor.trivia.R
import com.aitor.trivia.dao.ApiClient
import com.aitor.trivia.util.ThemeManager

class RegistroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.apply(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val etUser  = findViewById<EditText>(R.id.etRegUser)
        val etEmail = findViewById<EditText>(R.id.etRegEmail)
        val etPass  = findViewById<EditText>(R.id.etRegPass)
        val btnOk   = findViewById<Button>(R.id.btnConfirmarRegistro)
        val progress = findViewById<ProgressBar>(R.id.progressRegistro)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        btnOk.setOnClickListener {
            val user  = etUser.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val pass  = etPass.text.toString()

            if (user.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Introduce un correo electrónico válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (pass.length < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnOk.isEnabled = false
            progress.visibility = View.VISIBLE

            Thread {
                val result = ApiClient.register(user, pass, email)
                runOnUiThread {
                    progress.visibility = View.GONE
                    btnOk.isEnabled = true
                    if (result.success) {
                        Toast.makeText(this, "¡Cuenta creada! Ya puedes iniciar sesión", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, result.message.ifEmpty { "Error al registrar" }, Toast.LENGTH_SHORT).show()
                    }
                }
            }.start()
        }
    }
}
