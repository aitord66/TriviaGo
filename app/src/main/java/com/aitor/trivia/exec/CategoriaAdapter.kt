package com.aitor.trivia.exec

import Categoria
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aitor.trivia.R

class CategoriaAdapter(private val lista: List<Categoria>, private val onClick: (Categoria) -> Unit) :
    RecyclerView.Adapter<CategoriaAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tvNombre)
        val icono: TextView = view.findViewById(R.id.tvIcono)
        val container: LinearLayout = view.findViewById(R.id.container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_categoria, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cat = lista[position]
        holder.nombre.text = cat.nombre
        holder.icono.text = cat.icono
        holder.container.setBackgroundColor(cat.color)
        holder.itemView.setOnClickListener { onClick(cat) }
    }

    override fun getItemCount() = lista.size
}