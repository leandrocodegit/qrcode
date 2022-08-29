package com.api.qrcode.model

import com.api.qrcode.controller.response.ImagemReponse
import com.api.qrcode.enuns.Status
import org.springframework.data.mongodb.core.mapping.Document
import javax.persistence.Id

@Document
class Parceiro(
    @Id
    var cnpj: String,
    var nome: String,
    var status: Status,
    var comissao: Double,
    var logo: Imagem?
){
    constructor():this("","",Status.INATIVO,0.0,null)
    constructor(cnpj: String):this(cnpj,"",Status.INATIVO,0.0,null)
}