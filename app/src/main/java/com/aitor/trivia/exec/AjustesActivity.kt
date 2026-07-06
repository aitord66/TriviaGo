package com.aitor.trivia.exec

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aitor.trivia.R
import com.aitor.trivia.dao.ApiClient
import com.aitor.trivia.util.ThemeManager
import com.aitor.trivia.util.UserSession

class AjustesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.apply(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajustes)

        val username = intent.getStringExtra("USERNAME") ?: UserSession.getUsername(this)
        val idUser   = intent.getIntExtra("ID_USER", UserSession.getIdUser(this))

        // ── Cabecera ─────────────────────────────────────────
        findViewById<TextView>(R.id.tvAjustesUser).text = username
        findViewById<ImageButton>(R.id.btnVolverAjustes).setOnClickListener { finish() }

        // ── Selector de tema ─────────────────────────────────
        val rgTema   = findViewById<RadioGroup>(R.id.rgTema)
        val rbDark   = findViewById<RadioButton>(R.id.rbDark)
        val rbLight  = findViewById<RadioButton>(R.id.rbLight)

        when (ThemeManager.getSavedTheme(this)) {
            ThemeManager.LIGHT -> rbLight.isChecked = true
            else               -> rbDark.isChecked  = true
        }

        rgTema.setOnCheckedChangeListener { _, checkedId ->
            val newTheme = if (checkedId == R.id.rbLight) ThemeManager.LIGHT else ThemeManager.DARK
            if (newTheme != ThemeManager.getSavedTheme(this)) {
                ThemeManager.applyAndSave(this, newTheme)
                // Relanzar InicioActivity destruyendo toda la pila
                val intent = Intent(this, InicioActivity::class.java)
                    .addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK
                    )
                startActivity(intent)
            }
        }

        // ── Cambiar Contraseña (Con cierre de sesión automático) ──
        val etContrasenaActual = findViewById<EditText>(R.id.etContrasenaActual)
        val etNuevaContrasena  = findViewById<EditText>(R.id.etNuevaContrasena)
        val btnCambiarContra   = findViewById<Button>(R.id.btnCambiarContrasena)

        btnCambiarContra.setOnClickListener {
            val contraActual = etContrasenaActual.text.toString().trim()
            val nuevaContra  = etNuevaContrasena.text.toString().trim()


            if (contraActual.isEmpty() || nuevaContra.isEmpty()) {
                Toast.makeText(this, "⚠️ Completa ambos campos de contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nuevaContra.length < 6) {
                Toast.makeText(this, "⚠️ La nueva contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (contraActual == nuevaContra) {
                Toast.makeText(this, "⚠️ La nueva contraseña no puede ser igual a la actual", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            Thread {
                val resultado = ApiClient.changePassword(idUser, contraActual, nuevaContra)


                runOnUiThread {
                    if (resultado.success) {

                        Toast.makeText(this, "🔒 Contraseña actualizada. Inicia sesión de nuevo.", Toast.LENGTH_LONG).show()


                        UserSession.clear(this)


                        val intentLogin = Intent(this, LoginActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(intentLogin)


                        finish()

                    } else {

                        Toast.makeText(this, "❌ ${resultado.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }.start()
        }

        // ── Cerrar sesión ─────────────────────────────────────
        findViewById<Button>(R.id.btnCerrarSesion).setOnClickListener {
            UserSession.clear(this)
            startActivity(
                Intent(this, LoginActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }
}