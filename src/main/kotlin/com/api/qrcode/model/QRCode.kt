package com.api.qrcode.model

import com.api.qrcode.enuns.Status
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document
import java.sql.Timestamp
import javax.persistence.Id

@Document
class QRCode(
    @Id
    var id: ObjectId,
    var codigo: Int,
    var preco: Double,
    var parceiro: Parceiro?,
    var produto: Produto?,
    var desconto: Double?,
    var imagem: Imagem?,
    var status: Status,
    var isImpresso: Boolean
){
    constructor():this(ObjectId.get(),0,0.0,null, null, 0.0,null,Status.INATIVO,false){}
    constructor(id: ObjectId):this(id,0,0.0,null, null,0.0,null,Status.INATIVO,false){}
}