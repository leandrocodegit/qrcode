package com.api.qrcode.consumer

import com.api.qrcode.enuns.Status
import com.api.qrcode.rest.RestParceiro
import com.api.qrcode.rest.RestProduto
import com.api.qrcode.service.ParceiroService
import com.api.qrcode.service.ProdutoService
import com.api.qrcode.service.QRCodeService
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ProcessaParceiroImplement(
    private val qrCodeService: QRCodeService,
    private val parceiroService: ParceiroService,
    private val restParceiro: RestParceiro
): ProcessaMensagem {

    override fun processaUpdateStock(fromID: String) {}

    override fun processaCreateEntity(fromID: String) {
        parceiroService.createParceiro(restParceiro.getParceiro(fromID))
        processaStatusQRCode(fromID, Status.INATIVO)
    }

    override fun processaUpdateEntity(fromID: String) {
        parceiroService.atualizaParceiro(restParceiro.getParceiro(fromID)).apply {
            processaStatusQRCode(fromID, status.let {
                if(status != Status.ATIVO )
                     Status.INATIVO
                else
                    Status.ATIVO
            })
        }
    }

    override fun processaDeleteEntity(fromID: String) {
        processaStatusQRCode(fromID, Status.INATIVO)
        parceiroService.deleteParceiro(fromID)
    }

    override fun processaStatusQRCode(fromID: String, status: Status) {
        qrCodeService.alteraStatusListaQRCode(
            qrCodeService.listaTodosQRCodePorParceiro(Pageable.unpaged(), fromID).map {
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
    }

    override fun processaUpdatePrecoQRCode(fromID: String) {
        parceiroService.atualizaParceiro(restParceiro.getParceiro(fromID))
        qrCodeService.atualizaPrecoListaQRCode(
            qrCodeService.listaTodosQRCodePorParceiro(Pageable.unpaged(), fromID)
        )
    }
}