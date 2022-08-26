package com.api.qrcode.controller.request

import javax.validation.constraints.NotBlank

class QRCodeRequest(
    @field:NotBlank
    var id: String,
    @field:NotBlank
    var codigoProduto: String,
    @field:NotBlank
    var cnpj: String
)