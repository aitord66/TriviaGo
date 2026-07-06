package com.aitor.trivia.pojo

import com.google.gson.annotations.SerializedName

// Datos que enviamos al servidor PHP
data class CambiarContrasenaRequest(
    @SerializedName("id_user") val idUser: Int,
    @SerializedName("contrasenaActual") val contrasenaActual: String,
    @SerializedName("nuevaContrasena") val nuevaContrasena: String
)

// Respuesta que recibimos de change_password.php
data class CambiarContrasenaResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)