package com.aitor.trivia.dao

import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


object ApiClient {

    //Cambia esta URL por la dirección de tu servidor PHP
    const val BASE_URL = "https://lung-avert-pork.ngrok-free.dev/trivia_api"   // 10.0.2.2 = localhost desde el emulador
    //const val BASE_URL = "http://IP_SERVIDOR/trivia_api"  // Para dispositivo físico en la misma red

    // ------------------------------------------------------------------
    // Registro
    // ------------------------------------------------------------------
    data class RegisterResult(val success: Boolean, val message: String = "")

    fun register(username: String, password: String, email: String): RegisterResult {
        return try {
            val body = JSONObject().apply {
                put("username", username)
                put("password", password)
                put("email", email)
            }
            val resp = post("$BASE_URL/register.php", body)
            RegisterResult(resp.optBoolean("success"), resp.optString("message"))
        } catch (e: Exception) {
            RegisterResult(false, "Error de red: ${e.message}")
        }
    }

    // ------------------------------------------------------------------
    // Login
    // ------------------------------------------------------------------
    data class LoginResult(val success: Boolean, val idUser: Int = -1, val message: String = "")

    fun login(username: String, password: String): LoginResult {
        return try {
            val body = JSONObject().apply {
                put("username", username)
                put("password", password)
            }
            val resp = post("$BASE_URL/login.php", body)
            LoginResult(
                success = resp.optBoolean("success"),
                idUser  = resp.optInt("id_user", -1),
                message = resp.optString("message")
            )
        } catch (e: Exception) {
            LoginResult(false, -1, "Error de red: ${e.message}")
        }
    }

    // ------------------------------------------------------------------
    // Guardar puntuación
    // ------------------------------------------------------------------
    fun saveScore(idUser: Int, categoria: String, puntuacion: Int): Boolean {
        return try {
            val body = JSONObject().apply {
                put("id_user",    idUser)
                put("categoria",  categoria)
                put("puntuacion", puntuacion)
            }
            val resp = post("$BASE_URL/save_score.php", body)
            resp.optBoolean("success")
        } catch (e: Exception) {
            false
        }
    }

    // ------------------------------------------------------------------
    // Modelos para el Ranking
    // ------------------------------------------------------------------
    data class EntradaRanking(
        val username:   String,
        val categoria:  String,
        val puntuacion: Int,
        val fecha:      String
    )

    // NUEVO: Modelo contenedor para la respuesta completa del servidor
    data class RespuestaRanking(
        val ranking: List<EntradaRanking>,
        val tiempoRestanteSegundos: Long
    )

    // NUEVA FUNCIÓN: Obtiene el ranking filtrado por categoría y el contador de tiempo
    fun getRankingCompleto(limite: Int = 20, categoria: String? = null): RespuestaRanking? {
        return try {
            // Construimos la URL dinámica añadiendo la categoría si existe
            var urlString = "$BASE_URL/get_ranking.php?limit=$limite"
            if (!categoria.isNullOrBlank()) {
                val catCodificada = URLEncoder.encode(categoria, "UTF-8")
                urlString += "&categoria=$catCodificada"
            }

            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 5000
            conn.readTimeout = 5000

            val text = conn.inputStream.bufferedReader().readText()
            conn.disconnect()

            val json = JSONObject(text)
            if (!json.optBoolean("success")) return null

            // Extraer el contador de tiempo (mapeado desde el PHP)
            val tiempoRestante = json.optLong("tiempo_restante_segundos", 0L)

            // Procesar la lista de posiciones
            val arr = json.getJSONArray("ranking")
            val listaRanking = (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                EntradaRanking(
                    username   = o.optString("username"),
                    categoria  = o.optString("categoria"),
                    puntuacion = o.optInt("puntuacion"),
                    fecha      = o.optString("fecha")
                )
            }

            RespuestaRanking(listaRanking, tiempoRestante)
        } catch (e: Exception) {
            null
        }
    }

    // Mantenemos tu función antigua por si la usas en otra sección del proyecto
    fun getRanking(limite: Int = 20): List<EntradaRanking> {
        val res = getRankingCompleto(limite, null)
        return res?.ranking ?: emptyList()
    }

    // ------------------------------------------------------------------
    // Mejor puntuación propia en una categoría
    // ------------------------------------------------------------------
    fun getBestScore(idUser: Int, categoria: String): Int {
        return try {
            val body = JSONObject().apply {
                put("id_user",   idUser)
                put("categoria", categoria)
            }
            val resp = post("$BASE_URL/get_best_score.php", body)
            resp.optInt("mejor", 0)
        } catch (e: Exception) {
            0
        }
    }

    // ------------------------------------------------------------------
    // Helper POST genérico
    // ------------------------------------------------------------------

    // ------------------------------------------------------------------
    // Cambiar Contraseña
    // ------------------------------------------------------------------
    data class ChangePasswordResult(val success: Boolean, val message: String = "")

    fun changePassword(idUser: Int, contrasenaActual: String, nuevaContrasena: String): ChangePasswordResult {
        return try {
            val body = JSONObject().apply {
                put("id_user", idUser)
                put("contrasenaActual", contrasenaActual)
                put("nuevaContrasena", nuevaContrasena)
            }
            // Realiza la petición POST al nuevo script PHP
            val resp = post("$BASE_URL/change_password.php", body)
            ChangePasswordResult(
                success = resp.optBoolean("success"),
                message = resp.optString("message", "")
            )
        } catch (e: Exception) {
            ChangePasswordResult(false, "Error de red: ${e.message}")
        }
    }
    private fun post(urlStr: String, body: JSONObject): JSONObject {
        val conn = (URL(urlStr).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            doOutput = true
            connectTimeout = 5000
            readTimeout = 5000
        }
        OutputStreamWriter(conn.outputStream, "UTF-8").use { it.write(body.toString()) }
        val text = conn.inputStream.bufferedReader().readText()
        conn.disconnect()
        return JSONObject(text)
    }
}