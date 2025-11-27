package com.ifpr.androidapptemplate.baseclasses

data class Tesouro(
    val id: String = "",
    val nome: String = "",
    val descricao: String = "",
    val imageUrl: String? = null, // Link da foto no Firebase Storage
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val endereco: String = "", // Endereço aproximado
    val criadoPorUid: String = "", // UID do usuário que postou
    val criadoPorNome: String = "", // Nome do usuário
    val timestamp: Long = 0L // Para ordenar por mais recente
)

