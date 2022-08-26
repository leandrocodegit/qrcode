package com.api.qrcode.service

import com.api.qrcode.controller.request.QRCodeRequest
import com.api.qrcode.controller.request.QRCodeStatusRequest
import com.api.qrcode.enuns.CodeError
import com.api.qrcode.enuns.Status
import com.api.qrcode.exceptions.EntityResponseException
import com.api.qrcode.media.QRCodeGenarate
import com.api.qrcode.model.QRCode
import com.api.qrcode.repository.ImageStore
import com.api.qrcode.repository.QRCodeRepository
import com.api.qrcode.rest.RestParceiro
import com.api.qrcode.rest.RestProduto
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Value
import org.springframework.content.fs.io.FileSystemResourceLoader
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class QRCodeService(
    private val qrCodeRepository: QRCodeRepository,
    private val restProduto: RestProduto,
    private val restParceiro: RestParceiro,
    private val imageStore: ImageStore,
    private val qrcode: QRCodeGenarate
) {

    fun listaTodosQRCode(page: Pageable) = qrCodeRepository.findAll(page)

    fun buscaQRCode(id: String): QRCode {

        return when (id.toIntOrNull()) {
            is Int -> qrCodeRepository.findByCodigo(id.toInt()).orElseThrow {
                throw EntityResponseException("QRCode não encontrado", CodeError.FILE_ERROR)
            }
            null -> qrCodeRepository.findById(ObjectId(id)).orElseThrow {
                throw EntityResponseException("QRCode não encontrado", CodeError.FILE_ERROR)
            }
            else -> throw EntityResponseException("Id invalido", CodeError.FORMAT_INVALID)
        }
    }

    fun criarQRCode() =
        QRCode(
            ObjectId.get()
        ).apply {
            codigo = id.timestamp
            imagem = qrcode.gerarQR(id.toString(), id.timestamp)
            qrCodeRepository.save(this)
        }

    fun associarQRCode(request: QRCodeRequest) =
        buscaQRCode(request.id).apply {
            parceiro = restParceiro.getParceiro(request.cnpj)
            produto = restProduto.getProduto(request.codigoProduto)
        }.apply {
            if (parceiro?.status != Status.ATIVO)
                throw EntityResponseException("Operacao nao permitida parceiro inativo", CodeError.INACTIVE)
            if (produto?.status == false)
                throw EntityResponseException("Operacao nao permitida produto inativo", CodeError.INACTIVE)
        }.apply {
            preco = ((produto!!.preco * (parceiro!!.comissao / 100)) + produto!!.preco)
            qrCodeRepository.save(this)
        }

    fun atualizaStatusQRCode(request: QRCodeStatusRequest) =
        buscaQRCode(request.id.toString()).apply {
            if(request.status == Status.ATIVO) {
                if (produto == null || parceiro == null)
                    throw EntityResponseException("QRcode não foi associado", CodeError.INACTIVE)
                if (isValidaStatusParceiroProduto(produto!!.codigo, parceiro!!.cnpj).not())
                    throw EntityResponseException("Produto ou parceiro não esta ativos", CodeError.INACTIVE)
            }
        }.apply {
            status = request.status
            qrCodeRepository.save(this)
        }

    fun deleteQRCode(id: ObjectId, force: Boolean) {
        buscaQRCode(id.toString()).apply {
            if(isImpresso && force.not())
                throw EntityResponseException("Nao é possivel excluir um qrcode que já foi impresso", CodeError.INACTIVE)
            if (imagem != null)
                imageStore.unsetContent(imagem)
            qrCodeRepository.deleteById(id)
        }
    }

    fun marqueImpresso(id: ObjectId, isPrint: Boolean) =
    buscaQRCode(id.toString()).apply {
        isImpresso = isPrint
        qrCodeRepository.save(this)
    }.isImpresso

    private fun isValidaStatusParceiroProduto(codigo: String, cnpj: String) =
        (restParceiro.getParceiro(cnpj).status == Status.ATIVO
                        &&
        restProduto.getProduto(codigo).status)
}