package com.aitor.trivia.exec

import Categoria
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.aitor.trivia.R
import com.aitor.trivia.util.ThemeManager
import com.aitor.trivia.util.UserSession

class MenuActivity : AppCompatActivity() {

    private var username: String = "Jugador"
    private var idUser: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.apply(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        username = intent.getStringExtra("USERNAME") ?: UserSession.getUsername(this)
        idUser   = intent.getIntExtra("ID_USER", UserSession.getIdUser(this))

        findViewById<TextView>(R.id.tvUserName).text = "¡Hola, $username!"

        findViewById<Button>(R.id.btnRanking).setOnClickListener {
            startActivity(Intent(this, RankingActivity::class.java))
        }

        findViewById<Button>(R.id.btnSettings).setOnClickListener {
            startActivity(
                Intent(this, AjustesActivity::class.java)
                    .putExtra("USERNAME", username)
                    .putExtra("ID_USER", idUser)
            )
        }

        val categorias = listOf(
            Categoria("Historia", "📜", Color.parseColor("#FF8C00"), "historia.json"),
            Categoria("Ciencia", "🧪", Color.parseColor("#1565C0"), "ciencia.json"),
            Categoria("Geografía", "🌍", Color.parseColor("#2E7D32"), "geografia.json"),
            Categoria("Deportes", "⚽", Color.parseColor("#F57F17"), "deportes.json"),
            Categoria("Videojuegos", "🎮", Color.parseColor("#6A1B9A"), "videojuegos.json"),
            Categoria("Películas", "🎬", Color.parseColor("#B71C1C"), "peliculas.json"),
            Categoria("Música", "🎵", Color.parseColor("#00695C"), "musica.json")
        )

        val rv = findViewById<RecyclerView>(R.id.rvCategorias)
        rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv.adapter = CategoriaAdapter(categorias) { cat ->
            startActivity(
                Intent(this, JuegoActivity::class.java)
                    .putExtra("CATEGORIA", cat.nombre)
                    .putExtra("ARCHIVO",   cat.archivo)
                    .putExtra("USERNAME",  username)
                    .putExtra("ID_USER",   idUser)
            )
        }
        PagerSnapHelper().attachToRecyclerView(rv)

        findViewById<Button>(R.id.btnRandom).setOnClickListener {
            val cat = categorias.random()
            startActivity(
                Intent(this, JuegoActivity::class.java)
                    .putExtra("CATEGORIA", cat.nombre)
                    .putExtra("ARCHIVO",   cat.archivo)
                    .putExtra("USERNAME",  username)
                    .putExtra("ID_USER",   idUser)
            )
        }
    }
}
