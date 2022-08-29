package com.api.qrcode.build

import com.api.qrcode.enuns.Status
import com.api.qrcode.model.*
import org.bson.types.ObjectId
import java.util.*

class QRCodeBuild {

    companion object{

        var parceiro = Parceiro("","",Status.ATIVO,10.0,
            Imagem(
            UUID.fromString("d2c2a587-be9b-4423-ba03-742872aa8654").toString(),
            10,
            "image/png"))
        var produto = Produto("7000","Jogo de 6 taças", 170.0, Estoque(1L,10,0),
        "",
        "","Bohemia",true)

        fun qrcode() = createQRCode(ObjectId.get(), 0.0, Status.ATIVO)
        fun qrcode(id: ObjectId) = createQRCode(id, 0.0, Status.ATIVO)
        fun qrcode(id: ObjectId,  status: Status) = createQRCode(id,170.0, Status.ATIVO )
        fun qrcode(id: ObjectId, preco: Double, status: Status) = createQRCode(id, preco, status )

        private fun createQRCode(id: ObjectId, preco: Double, status: Status) = QRCode(id, id.timestamp, preco, parceiro, produto, 0.0, null, status,false )

    }
}