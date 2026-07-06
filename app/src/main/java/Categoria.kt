data class Categoria(
    val nombre: String,
    val icono: String,
    val color: Int,
    val archivo: String = "${nombre.lowercase().replace("í","i").replace("é","e").replace("ó","o")}.json"
)
