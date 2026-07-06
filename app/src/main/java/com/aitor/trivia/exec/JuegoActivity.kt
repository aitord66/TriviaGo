package com.aitor.trivia.exec

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aitor.trivia.R
import com.aitor.trivia.dao.ApiClient
import com.aitor.trivia.pojo.CategoriaDatos
import com.aitor.trivia.pojo.Pregunta
import com.aitor.trivia.util.ThemeManager
import com.google.gson.Gson

class JuegoActivity : AppCompatActivity() {

    private lateinit var tvCategoria:TextView
    private lateinit var tvPreguntaNum:TextView
    private lateinit var tvPuntuacion:TextView
    private lateinit var tvPregunta:TextView
    private lateinit var tvTiempo:TextView
    private lateinit var tvFeedback:TextView
    private lateinit var progressBar:ProgressBar
    private lateinit var ivImagen:ImageView
    private lateinit var btnOpciones:List<Button>

    private var preguntas:List<Pregunta> = emptyList()
    private var preguntaActual = 0
    private var puntuacion = 0
    private var timer:CountDownTimer? = null

    private val TIEMPO_MS = 20_000L
    private val PUNTOS_MAX = 1000

    private lateinit var nombreCategoria:String
    private lateinit var archivoJson:String
    private lateinit var username:String
    private var idUser = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.apply(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_juego)

        nombreCategoria = intent.getStringExtra("CATEGORIA") ?: "Historia"
        archivoJson = intent.getStringExtra("ARCHIVO")   ?: "historia.json"
        username = intent.getStringExtra("USERNAME")  ?: "Jugador"
        idUser = intent.getIntExtra("ID_USER", -1)

        tvCategoria = findViewById(R.id.tvJuegoCategoria)
        tvPreguntaNum = findViewById(R.id.tvNumeroPregunta)
        tvPuntuacion = findViewById(R.id.tvPuntuacionJuego)
        tvPregunta = findViewById(R.id.tvEnunciado)
        tvTiempo = findViewById(R.id.tvTiempo)
        tvFeedback = findViewById(R.id.tvFeedback)
        progressBar = findViewById(R.id.progressTiempo)
        ivImagen = findViewById(R.id.ivPreguntaImagen)

        btnOpciones = listOf(
            findViewById(R.id.btnOpcion1),
            findViewById(R.id.btnOpcion2),
            findViewById(R.id.btnOpcion3),
            findViewById(R.id.btnOpcion4)
        )

        tvCategoria.text = nombreCategoria.uppercase()
        cargarPreguntas()
        mostrarPregunta()
    }

    private fun cargarPreguntas() {
        try {
            val json = assets.open(archivoJson).bufferedReader().use { it.readText() }
            val datos = Gson().fromJson(json, CategoriaDatos::class.java)
            preguntas = datos.preguntas.shuffled()
        } catch (e: Exception) {
            Toast.makeText(this, "Error cargando preguntas", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun mostrarPregunta() {
        if (preguntaActual >= preguntas.size) { terminarJuego(); return }

        val pregunta = preguntas[preguntaActual]
        tvPreguntaNum.text = "${preguntaActual + 1} / ${preguntas.size}"
        tvPuntuacion.text = "⭐ $puntuacion"
        tvPregunta.text = pregunta.enunciado
        tvFeedback.visibility = View.INVISIBLE

        // Mostrar imagen si la pregunta tiene una asociada
        mostrarImagen(pregunta.imagen)

        val opciones = pregunta.opciones.shuffled()
        val colorNormal = androidx.core.content.ContextCompat.getColor(this, R.color.btn_opcion_dark)
        btnOpciones.forEachIndexed { i, btn ->
            btn.text = opciones[i].texto
            btn.isEnabled = true
            btn.backgroundTintList = android.content.res.ColorStateList.valueOf(colorNormal)
            btn.setTextColor(Color.WHITE)
            btn.tag = opciones[i].es_correcta
        }

        iniciarTimer()
    }

    /**
     * Busca el drawable por nombre en res/drawable.
     * Si existe lo muestra ocupando la mitad superior de la card.
     * Si es null o no se encuentra, oculta el ImageView completamente.
     */
    private fun mostrarImagen(nombreImagen: String?) {
        if (nombreImagen.isNullOrBlank()) {
            ivImagen.visibility = View.GONE
            return
        }
        val resId = resources.getIdentifier(nombreImagen, "drawable", packageName)
        if (resId != 0) {
            ivImagen.setImageResource(resId)
            ivImagen.visibility = View.VISIBLE
        } else {
            ivImagen.visibility = View.GONE
        }
    }

    private fun iniciarTimer() {
        timer?.cancel()
        progressBar.max = TIEMPO_MS.toInt()
        progressBar.progress = TIEMPO_MS.toInt()

        timer = object : CountDownTimer(TIEMPO_MS, 100) {
            override fun onTick(ms: Long) {
                progressBar.progress = ms.toInt()
                val seg = (ms / 1000).toInt()
                tvTiempo.text = "$seg"
                if (seg <= 5) tvTiempo.setTextColor(Color.parseColor("#FF5252"))
                else          tvTiempo.setTextColor(Color.parseColor("#FFD740"))
            }
            override fun onFinish() {
                tvTiempo.text = "0"
                mostrarFeedback("⏱ ¡Tiempo!", "#FF5252")
                Handler(Looper.getMainLooper()).postDelayed({
                    preguntaActual++; mostrarPregunta()
                }, 1500)
            }
        }.start()
    }

    fun onOpcionClick(view: View) {
        timer?.cancel()
        val btn = view as Button
        val esCorrecta = btn.tag as Boolean
        val segundosRestantes = progressBar.progress / 1000

        btnOpciones.forEach { it.isEnabled = false }

        if (esCorrecta) {
            val pts = when {
                segundosRestantes >= 18 -> 1000
                segundosRestantes >= 14 -> 750
                segundosRestantes >= 7 -> 500
                segundosRestantes > 0 -> 250
                else -> 0
            }
            puntuacion += pts
            tvPuntuacion.text = "⭐ $puntuacion"
            mostrarFeedback("✔ +$pts pts", "#69F0AE")
        } else {
            mostrarFeedback("✘ ¡Incorrecto!", "#FF5252")
        }

        Handler(Looper.getMainLooper()).postDelayed({
            preguntaActual++; mostrarPregunta()
        }, 1500)
    }



    private fun mostrarFeedback(msg: String, colorHex: String) {
        tvFeedback.text = msg
        tvFeedback.setTextColor(Color.parseColor(colorHex))
        tvFeedback.visibility = View.VISIBLE
    }

    private fun terminarJuego() {
        if (idUser != -1) {
            Thread { ApiClient.saveScore(idUser, nombreCategoria, puntuacion) }.start()
        }
        startActivity(
            Intent(this, ResultadoActivity::class.java)
                .putExtra("PUNTUACION",puntuacion)
                .putExtra("CATEGORIA",nombreCategoria)
                .putExtra("USERNAME",username)
                .putExtra("ID_USER",idUser)
                .putExtra("MEJOR_PUNTUACION",0)
                .putExtra("TOTAL_PREGUNTAS",preguntas.size)
        )
        finish()
    }

    override fun onDestroy() { super.onDestroy(); timer?.cancel() }
}
