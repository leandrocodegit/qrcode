package com.api.qrcode.controller.response

import com.api.qrcode.enuns.Status
import com.api.qrcode.model.Imagem
import com.api.qrcode.model.Parceiro

class QRCodeResponse(
    var id: String,
    var codigo: Int,
    var preco: Double,
    var parceiro: Parceiro?,
    var produto: ProdutoResponse?,
    var desconto: Double?,
    var imagem: ImagemReponse?,
    var status: Status
)