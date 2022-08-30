package com.api.qrcode.consumer

import com.api.qrcode.enuns.Status
import com.api.qrcode.exceptions.EntityResponseException
import com.api.qrcode.rest.RestProduto
import com.api.qrcode.service.ProdutoService
import com.api.qrcode.service.QRCodeService
import com.google.gson.Gson
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service
import org.springframework.amqp.core.Message
import org.springframework.data.domain.Pageable

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
            val receive: MensagemReceive = Gson().fromJson(body, MensagemReceive::class.java)
            println(body)
            if (receive.typeSend == TypeSend.ENTITY)
                when (receive.operacao) {
                    TypeSend.CREATE -> processaParceiro.processaCreateEntity(receive.fromID)
                    TypeSend.DELETE -> processaParceiro.processaDeleteEntity(receive.fromID)
                }
            else if (receive.typeSend == TypeSend.CHANGE)
                when (receive.operacao) {
                    TypeSend.UPDATE -> processaParceiro.processaUpdateEntity(receive.fromID)
                    TypeSend.PRICE -> processaParceiro.processaUpdatePrecoQRCode(receive.fromID)
                    TypeSend.ATIVE -> processaParceiro.processaUpdateEntity(receive.fromID)
                    TypeSend.INATIVE -> processaParceiro.processaUpdateEntity(receive.fromID)
                    TypeSend.STOCK -> processaParceiro.processaUpdateStock(receive.fromID)
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
            val receive: MensagemReceive = Gson().fromJson(body, MensagemReceive::class.java)
            println(body)
            if (receive.typeSend == TypeSend.ENTITY)
                when (receive.operacao) {
                    TypeSend.CREATE -> processaProduto.processaCreateEntity(receive.fromID)
                    TypeSend.DELETE -> processaProduto.processaDeleteEntity(receive.fromID)
                }
            else if (receive.typeSend == TypeSend.CHANGE)
                when (receive.operacao) {
                    TypeSend.UPDATE -> processaProduto.processaUpdateEntity(receive.fromID)
                    TypeSend.PRICE -> processaProduto.processaUpdatePrecoQRCode(receive.fromID)
                    TypeSend.ATIVE -> processaProduto.processaStatusQRCode(receive.fromID, Status.ATIVO)
                    TypeSend.INATIVE -> processaProduto.processaStatusQRCode(receive.fromID, Status.INATIVO)
                    TypeSend.STOCK -> processaProduto.processaUpdateStock(receive.fromID)
                }
        } catch (ex: Exception) {
            println("Erro ao processar mensagem")
            ex.printStackTrace()
        }
    }

}