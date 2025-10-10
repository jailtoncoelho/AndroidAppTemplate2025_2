package com.ifpr.androidapptemplate.baseclasses

data class Item(
    var endereco: String? = null,
    var CNPJ: String? = null,
    var RazaoSocial: String? = null,
    var Servico: String? = null,
    val base64Image: String? = null,
    val imageUrl: String? = null
)
