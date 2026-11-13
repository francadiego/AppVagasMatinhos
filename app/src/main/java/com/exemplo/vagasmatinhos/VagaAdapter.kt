package com.exemplo.vagasmatinhos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VagaAdapter(
    private val listaVagas: List<Vaga>,
    private val onItemClick: (Vaga) -> Unit // Callback de clique
) : RecyclerView.Adapter<VagaAdapter.VagaViewHolder>() {

    class VagaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTitulo: TextView = itemView.findViewById(R.id.txtTituloVaga)
        val txtEmpresa: TextView = itemView.findViewById(R.id.txtEmpresa)
        val txtSalario: TextView = itemView.findViewById(R.id.txtSalario)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VagaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_vaga, parent, false)
        return VagaViewHolder(view)
    }

    override fun onBindViewHolder(holder: VagaViewHolder, position: Int) {
        val vaga = listaVagas[position]
        holder.txtTitulo.text = vaga.titulo
        holder.txtEmpresa.text = vaga.empresa
        holder.txtSalario.text = vaga.salario

        // Configura o evento de clique no item inteiro
        holder.itemView.setOnClickListener {
            onItemClick(vaga)
        }
    }

    override fun getItemCount(): Int = listaVagas.size
}