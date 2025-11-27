package com.ifpr.androidapptemplate.baseclasses

data class RpgCharacter(
    var documentId: String? = null,

    var id_usuario: String? = null,
    var nome_personagem: String = "",
    var conceito_personagem: String = "",
    var raca_personagem: String = "",
    var classe_personagem: String = "",
    var historia_completa: String = "",
    var data_criacao: Long? = System.currentTimeMillis() // Use Long? para permitir null/0
) {
    fun getResumo(): String {
        return "$raca_personagem $classe_personagem. Conceito: $conceito_personagem"
    }
}