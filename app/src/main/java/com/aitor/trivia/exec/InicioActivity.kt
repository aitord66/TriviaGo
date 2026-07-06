package com.aitor.trivia.exec

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.aitor.trivia.R
import com.aitor.trivia.util.ThemeManager
import com.aitor.trivia.util.UserSession

class InicioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.apply(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        Handler(Looper.getMainLooper()).postDelayed({
            // Si ya hay sesión guardada, ir directo al menú
            if (UserSession.isLoggedIn(this)) {
                startActivity(
                    Intent(this, MenuActivity::class.java)
                        .putExtra("USERNAME", UserSession.getUsername(this))
                        .putExtra("ID_USER",  UserSession.getIdUser(this))
                )
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, 2500)
    }
}
