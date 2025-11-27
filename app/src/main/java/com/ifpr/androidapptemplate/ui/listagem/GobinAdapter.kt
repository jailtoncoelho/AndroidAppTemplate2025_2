package com.ifpr.androidapptemplate.ui.listagem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ifpr.androidapptemplate.R
import com.ifpr.androidapptemplate.rpg.data.Goblin

class GoblinAdapter(private var goblinList: List<Goblin>) :
    RecyclerView.Adapter<GoblinAdapter.GoblinViewHolder>() {

    class GoblinViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // 🎯 CORREÇÃO: IDs alinhados com item_character_card.xml
        val nome: TextView = itemView.findViewById(R.id.tvCharacterName)
        val ocupacao: TextView = itemView.findViewById(R.id.tvCharacterClassRace)
        val atributos: TextView = itemView.findViewById(R.id.tvCharacterConcept)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoblinViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_character_card, parent, false)
        return GoblinViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoblinViewHolder, position: Int) {
        val currentGoblin = goblinList[position]

        // Nome e descritor
        holder.nome.text = "${currentGoblin.nome} ${currentGoblin.descritor}"

        // Ocupação
        holder.ocupacao.text = "Ocupação: ${currentGoblin.ocupacao}"

        // Atributos (usando o TextView tvCharacterConcept)
        holder.atributos.text = "C: ${currentGoblin.atributos.combate} | H: ${currentGoblin.atributos.habilidade} | N: ${currentGoblin.atributos.nocao} | V: ${currentGoblin.atributos.vitalidade}"
    }

    override fun getItemCount() = goblinList.size

    fun updateList(newList: List<Goblin>) {
        goblinList = newList
        notifyDataSetChanged()
    }
}