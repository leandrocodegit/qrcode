package com.api.qrcode.controller.response

import com.api.qrcode.model.Estoque

class ProdutoResponse(
    var codigo: String,
    var descricao: String,
    var estoque: Estoque,
    var imageOriginal: String,
    var imageThumbnail: String
)