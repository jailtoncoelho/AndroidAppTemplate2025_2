package com.ifpr.androidapptemplate.baseclasses

data class Item(
    var endereco: String? = null,
    val base64Image: String? = null,
    var CNPJ: String? = null,
    var RazaoSocial: String? = null,
    var Servico: String? = null
)
