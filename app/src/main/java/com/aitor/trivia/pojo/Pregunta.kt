package com.aitor.trivia.pojo

data class Opcion(
    val texto: String,
    val es_correcta: Boolean
)

data class Pregunta(
    val id: Int,
    val enunciado: String,
    val opciones: List<Opcion>,
    val imagen: String? = null   // nombre del archivo en res/drawable, sin extensión
                                  // ej: "colon_america" → R.drawable.colon_america
                                  // Si es null o no existe, la imagen queda oculta
)

data class CategoriaDatos(
    val categoria: String,
    val preguntas: List<Pregunta>
)
