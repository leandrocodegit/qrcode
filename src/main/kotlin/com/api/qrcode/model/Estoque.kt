package com.api.qrcode.model

import javax.persistence.*

class Estoque(
        var id: Long,
        var estoqueAtual: Int,
        var reserva: Int,
)