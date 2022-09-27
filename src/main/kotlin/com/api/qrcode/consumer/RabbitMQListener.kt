package com.api.qrcode.consumer

import com.api.qrcode.enuns.Status
import com.api.qrcode.rest.RestProduto
import com.api.qrcode.service.ProdutoService
import com.api.qrcode.service.QRCodeService
import com.google.gson.Gson
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service
import org.springframework.amqp.core.Message

@Service
class RabbitMQListener(
    private val qrCodeService: QRCodeService,
    private val produtoService: ProdutoService,
    private val restProduto: RestProduto,
    private val processaProduto: ProcessaProdutoImplement,
    private val processaParceiro: ProcessaParceiroImplement
) {


    @RabbitListener(queues = ["QUEUE_NOTIFICACAO-PARCEIRO"])
    fun receiveParceiroMessage(message: Message) {

        try {
            val body = message.body?.let { String(it) }
            val receive: Mensagem = Gson().fromJson(body, Mensagem::class.java)
            println(body)
            if (receive.typeSend == TypeSend.ENTITY)
                when (receive.status) {
                    TypeSend.CREATE -> processaParceiro.processaCreateEntity(receive.fromId)
                    TypeSend.DELETE -> processaParceiro.processaDeleteEntity(receive.fromId)
                }
            else if (receive.typeSend == TypeSend.CHANGE)
                when (receive.status) {
                    TypeSend.UPDATE -> processaParceiro.processaUpdateEntity(receive.fromId)
                    TypeSend.PRICE -> processaParceiro.processaUpdatePrecoQRCode(receive.fromId)
                    TypeSend.ATIVE -> processaParceiro.processaUpdateEntity(receive.fromId)
                    TypeSend.INATIVE -> processaParceiro.processaUpdateEntity(receive.fromId)
                    TypeSend.STOCK -> processaParceiro.processaUpdateStock(receive.fromId)
                }

        } catch (ex: Exception) {
            println("Erro ao processar mensagem")
            ex.printStackTrace()
        }
    }

    @RabbitListener(queues = ["QUEUE_NOTIFICACAO-PRODUTO"])
    fun receiveProdutoMessage(message: Message) {

        try {
            val body = message.body?.let { String(it) }
            val receive: Mensagem = Gson().fromJson(body, Mensagem::class.java)
            println(body)
            if (receive.typeSend == TypeSend.ENTITY)
                when (receive.status) {
                    TypeSend.CREATE -> processaProduto.processaCreateEntity(receive.fromId)
                    TypeSend.DELETE -> processaProduto.processaDeleteEntity(receive.fromId)
                }
            else if (receive.typeSend == TypeSend.CHANGE)
                when (receive.status) {
                    TypeSend.UPDATE -> processaProduto.processaUpdateEntity(receive.fromId)
                    TypeSend.PRICE -> processaProduto.processaUpdatePrecoQRCode(receive.fromId)
                    TypeSend.ATIVE -> processaProduto.processaStatusQRCode(receive.fromId, Status.ATIVO)
                    TypeSend.INATIVE -> processaProduto.processaStatusQRCode(receive.fromId, Status.INATIVO)
                    TypeSend.STOCK -> processaProduto.processaUpdateStock(receive.fromId)
                }
        } catch (ex: Exception) {
            println("Erro ao processar mensagem")
            ex.printStackTrace()
        }
    }

}