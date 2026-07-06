package com.aitor.trivia.exec

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aitor.trivia.R
import com.aitor.trivia.dao.ApiClient
import com.aitor.trivia.util.ThemeManager
import java.util.Locale

class RankingActivity : AppCompatActivity() {

    private lateinit var rv: RecyclerView
    private lateinit var progress: ProgressBar
    private lateinit var tvSin: TextView
    private lateinit var tvContador: TextView
    private lateinit var spinner: Spinner

    private var countdownTimer: CountDownTimer? = null

    private val categorias = listOf(
        "Todas", "Historia", "Ciencia", "Geografía",
        "Deportes", "Videojuegos", "Películas", "Música"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.apply(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)

        rv       = findViewById(R.id.rvRanking)
        progress = findViewById(R.id.progressRanking)
        tvSin    = findViewById(R.id.tvSinDatos)
        tvContador = findViewById(R.id.tvContadorReinicio)
        spinner  = findViewById(R.id.spinnerCategoriaRanking)

        rv.layoutManager = LinearLayoutManager(this)

        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapterSpinner

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val catSeleccionada = categorias[position]
                val filtro = if (catSeleccionada == "Todas") null else catSeleccionada
                obtenerDatosRanking(filtro)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        findViewById<ImageButton>(R.id.btnVolverRanking).setOnClickListener { finish() }
    }

    private fun obtenerDatosRanking(categoria: String?) {
        progress.visibility = View.VISIBLE
        rv.visibility = View.GONE
        tvSin.visibility = View.GONE

        Thread {
            val respuestaApi = ApiClient.getRankingCompleto(30, categoria)

            runOnUiThread {
                progress.visibility = View.GONE

                // Si la llamada falla completamente
                if (respuestaApi == null) {
                    tvSin.visibility = View.VISIBLE
                    return@runOnUiThread
                }

                // Iniciar el reloj SIEMPRE, haya datos o no
                // Así cuando el ranking está vacío el contador sigue corriendo
                iniciarRelojReinicio(respuestaApi.tiempoRestanteSegundos)

                if (respuestaApi.ranking.isEmpty()) {
                    tvSin.visibility = View.VISIBLE
                } else {
                    rv.visibility = View.VISIBLE
                    rv.adapter = RankingAdapter(respuestaApi.ranking)
                }
            }
        }.start()
    }

    private fun iniciarRelojReinicio(segundosTotales: Long) {
        countdownTimer?.cancel()

        countdownTimer = object : CountDownTimer(segundosTotales * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val totalSegundos = millisUntilFinished / 1000
                val horas   = totalSegundos / 3600
                val minutos = (totalSegundos % 3600) / 60
                val segundos = totalSegundos % 60

                tvContador.text = String.format(
                    Locale.getDefault(),
                    "Reiniciando en: %02d:%02d:%02d", horas, minutos, segundos
                )
            }

            override fun onFinish() {
                tvContador.text = "Reiniciando ranking..."
                // Recargar con el filtro activo en ese momento
                val posicion = spinner.selectedItemPosition
                val filtro = if (categorias[posicion] == "Todas") null else categorias[posicion]
                obtenerDatosRanking(filtro)
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countdownTimer?.cancel()
    }

    // ── Adaptador ─────────────────────────────────────────────
    inner class RankingAdapter(
        private val lista: List<ApiClient.EntradaRanking>
    ) : RecyclerView.Adapter<RankingAdapter.VH>() {

        inner class VH(v: View) : RecyclerView.ViewHolder(v) {
            val tvPos:  TextView = v.findViewById(R.id.tvPosicion)
            val tvUser: TextView = v.findViewById(R.id.tvUsuarioRanking)
            val tvPts:  TextView = v.findViewById(R.id.tvPuntosRanking)
            val tvCat:  TextView = v.findViewById(R.id.tvCategoriaRanking)
        }

        override fun onCreateViewHolder(p: ViewGroup, t: Int) =
            VH(LayoutInflater.from(p.context).inflate(R.layout.item_ranking, p, false))

        override fun getItemCount() = lista.size

        override fun onBindViewHolder(h: VH, pos: Int) {
            val e = lista[pos]
            h.tvPos.text  = when (pos) { 0 -> "🥇"; 1 -> "🥈"; 2 -> "🥉"; else -> "${pos + 1}" }
            h.tvUser.text = e.username
            h.tvPts.text  = "${e.puntuacion} pts"
            h.tvCat.text  = e.categoria
        }
    }
}