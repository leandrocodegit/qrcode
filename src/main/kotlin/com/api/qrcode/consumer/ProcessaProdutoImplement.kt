package com.api.qrcode.consumer

import com.api.qrcode.enuns.Status
import com.api.qrcode.rest.RestProduto
import com.api.qrcode.service.ProdutoService
import com.api.qrcode.service.QRCodeService
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ProcessaProdutoImplement(
    private val qrCodeService: QRCodeService,
    private val produtoService: ProdutoService,
    private val restProduto: RestProduto
): ProcessaMensagem {

    override fun processaUpdateStock(fromID: String) {
        produtoService.atualizaProduto(restProduto.getProduto(fromID)).apply {
            if (estoque.estoqueAtual <= 0)
                processaStatusQRCode(codigo, Status.INATIVO)
        }
    }

    override fun processaCreateEntity(fromID: String) {
        produtoService.createProduto(restProduto.getProduto(fromID))
        processaStatusQRCode(fromID, Status.INATIVO)
    }

    override fun processaUpdateEntity(fromID: String) {
        produtoService.atualizaProduto(restProduto.getProduto(fromID))
        processaUpdatePrecoQRCode(fromID)
    }

    override fun processaDeleteEntity(fromID: String) {
        processaStatusQRCode(fromID, Status.INATIVO)
        produtoService.deleteProduto(fromID)
    }

    override fun processaStatusQRCode(fromID: String, status: Status) {
        processaUpdateEntity(fromID)
        qrCodeService.alteraStatusListaQRCode(
            qrCodeService.listaTodosQRCodePorProduto(Pageable.unpaged(), fromID).map {
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

    override fun processaUpdatePrecoQRCode(fromID: String) {
        produtoService.atualizaProduto(restProduto.getProduto(fromID))
        qrCodeService.atualizaPrecoListaQRCode(
            qrCodeService.listaTodosQRCodePorProduto(Pageable.unpaged(), fromID)
        )
    }
}