package com.ifpr.androidapptemplate.rpg.data

data class Atributos(
    var combate: Int = 2,
    var habilidade: Int = 2,
    var nocao: Int = 2,
    var vitalidade: Int = 2
)

data class Goblin(
    var id: String = "",
    var nome: String = "",
    var ocupacao: String = "",
    var descritor: String = "",
    var caracteristica: String = "",

    var nivel: Int = 1,
    // CORREÇÃO ESTRUTURAL: A Ficha agora contém um objeto 'atributos'
    var atributos: Atributos = Atributos(),

    var ferimentos: Int = 0,
    var equipamento: List<String> = emptyList()
)