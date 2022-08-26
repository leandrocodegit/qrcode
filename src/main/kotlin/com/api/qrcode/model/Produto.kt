package com.api.qrcode.model

import org.springframework.data.web.JsonPath
import javax.persistence.Column
import javax.persistence.Id

class Produto(
    @Id
    var codigo: String,
    var descricao: String,
    var preco: Double = 0.0,
    var estoque: Estoque,
    var imageOriginal: String,
    var imageThumbnail: String,
    var status: Boolean
)
