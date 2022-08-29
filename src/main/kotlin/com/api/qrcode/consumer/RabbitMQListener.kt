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
    private val restProduto: RestProduto
) {


    @RabbitListener(queues = ["QUEUE_NOTIFICACAO-PARCEIRO"])
    fun receiveParceiroMessage(message: Message) {

        val body = message.body?.let { String(it) }
        val receive: MensagemReceive = Gson().fromJson(body, MensagemReceive::class.java)
        if (receive.typeSend == TypeSend.CHANGE)
            qrCodeService.alteraStatusListaQRCode(
                qrCodeService.listaTodosQRCodePorParceiro(Pageable.unpaged(), receive.fromID), Status.INATIVO.let {
                    if (receive.operacao == TypeSend.ATIVE)
                        Status.ATIVO
                    else
                        Status.INATIVO
                })
    }

    @RabbitListener(queues = ["QUEUE_NOTIFICACAO-PRODUTO"])
    fun receiveProdutoMessage(message: Message) {

        try {
            val body = message.body?.let { String(it) }
            val receive: MensagemReceive = Gson().fromJson(body, MensagemReceive::class.java)
            println(body)
            if (receive.typeSend == TypeSend.ENTITY)
                when (receive.operacao) {
                    TypeSend.CREATE -> processaCreateProduto(receive.fromID)
                    TypeSend.DELETE -> processaDelete(receive.fromID)
                }
            else if (receive.typeSend == TypeSend.CHANGE)
                when (receive.operacao) {
                    TypeSend.UPDATE -> processaUpdateQRCode(receive.fromID)
                    TypeSend.PRICE -> processaUpdatePrecoQRCode(receive.fromID)
                    TypeSend.ATIVE -> processaStatusQRCode(receive.fromID, Status.ATIVO)
                    TypeSend.INATIVE -> processaStatusQRCode(receive.fromID, Status.INATIVO)
                    TypeSend.STOCK -> processaUpdateStock(receive.fromID)
                }
        } catch (ex: Exception) {
            println("Erro ao processar mensagem")
        }
    }


    private fun processaUpdateStock(codigo: String) {
        produtoService.atualizaProduto(restProduto.getProduto(codigo)).apply {
            if (estoque.estoqueAtual <= 0)
                processaStatusQRCode(codigo, Status.INATIVO)
        }
    }

    private fun processaCreateProduto(codigo: String) {
        produtoService.createProduto(restProduto.getProduto(codigo))
        processaUpdatePrecoQRCode(codigo)
        processaStatusQRCode(codigo, Status.INATIVO)
    }

    private fun processaUpdateQRCode(codigo: String) {
        produtoService.atualizaProduto(restProduto.getProduto(codigo))
    }

    private fun processaDelete(codigo: String) {
        processaStatusQRCode(codigo, Status.INATIVO)
        produtoService.deleteProduto(codigo)
    }

    private fun processaStatusQRCode(codigo: String, status: Status) {
        qrCodeService.alteraStatusListaQRCode(
            qrCodeService.listaTodosQRCodePorProduto(Pageable.unpaged(), codigo).map {
                it.apply {
                    if (status == Status.ATIVO) {
                        if (it.produto != null)
                            if (it.produto!!.status.not())
                                it.status = Status.INATIVO
                        if (it.parceiro != null)
                            if (it.parceiro!!.status == Status.INATIVO)
                                it.status = Status.INATIVO
                        if (it.parceiro == null || it.produto == null)
                            it.status = Status.INATIVO
                    }
                }
            }.toList(), status
        )
        println("STATUS LIST")
    }

    private fun processaUpdatePrecoQRCode(codigo: String) {
        produtoService.atualizaProduto(restProduto.getProduto(codigo))
        qrCodeService.atualizaPrecoListaQRCode(
            qrCodeService.listaTodosQRCodePorProduto(Pageable.unpaged(), codigo)
        )
    }

}