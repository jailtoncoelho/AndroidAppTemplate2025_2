package com.ifpr.androidapptemplate.baseclasses

data class Item(
    var nome_produto: String? = null,
    var descricao: String? = null,
    var valor: Float? = null,
    var estoque: Int? = null,
    var proporcao: String? = null,
    val base64Image: String? = null,
    val imageUrl: String? = null
)
