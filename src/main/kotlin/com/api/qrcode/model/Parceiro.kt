package com.api.qrcode.model

import com.api.qrcode.controller.response.ImagemReponse
import com.api.qrcode.enuns.Status

class Parceiro(
    var cnpj: String,
    var nome: String,
    var status: Status,
    var comissao: Double,
    var logo: Imagem?
)