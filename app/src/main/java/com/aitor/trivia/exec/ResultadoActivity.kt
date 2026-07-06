package com.aitor.trivia.exec

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.aitor.trivia.R
import com.aitor.trivia.dao.ApiClient
import com.aitor.trivia.util.ThemeManager

class ResultadoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.apply(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultado)

        val puntuacion      = intent.getIntExtra("PUNTUACION", 0)
        val categoria       = intent.getStringExtra("CATEGORIA") ?: ""
        val username        = intent.getStringExtra("USERNAME") ?: "Jugador"
        val idUser          = intent.getIntExtra("ID_USER", -1)
        val totalPreguntas  = intent.getIntExtra("TOTAL_PREGUNTAS", 0)

        val tvPuntuacion = findViewById<TextView>(R.id.tvResultadoPuntuacion)
        val tvCategoria  = findViewById<TextView>(R.id.tvResultadoCategoria)
        val tvMensaje    = findViewById<TextView>(R.id.tvResultadoMensaje)
        val tvMejor      = findViewById<TextView>(R.id.tvMejorPuntuacion)
        val btnReintentar = findViewById<Button>(R.id.btnJugarOtraVez)
        val btnMenu       = findViewById<Button>(R.id.btnVolverMenu)

        tvPuntuacion.text = "$puntuacion"
        tvCategoria.text  = categoria.uppercase()
        tvMensaje.text    = when {
            puntuacion >= 7000 -> "¡LEGENDARIO! 🏆"
            puntuacion >= 4000 -> "¡Excelente! 🌟"
            puntuacion >= 1500 -> "¡Bien hecho! 👍"
            else               -> "¡Sigue intentándolo! 💪"
        }
        tvMejor.text = "Consultando récord…"

        // Consultar mejor puntuación en background
        if (idUser != -1) {
            Thread {
                val mejor = ApiClient.getBestScore(idUser, categoria)
                runOnUiThread {
                    if (puntuacion >= mejor) {
                        tvMejor.text = "🎉 ¡NUEVO RÉCORD PERSONAL!"
                        tvMejor.setTextColor(Color.parseColor("#FFD740"))
                    } else {
                        tvMejor.text = "Tu récord: $mejor pts"
                    }
                }
            }.start()
        }

        btnReintentar.setOnClickListener { finish() }

        btnMenu.setOnClickListener {
            startActivity(
                Intent(this, MenuActivity::class.java)
                    .putExtra("USERNAME", username)
                    .putExtra("ID_USER", idUser)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
            finish()
        }
    }
}
