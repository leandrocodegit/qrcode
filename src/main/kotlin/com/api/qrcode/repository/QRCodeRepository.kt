package com.api.qrcode.repository

import com.api.qrcode.model.Parceiro
import com.api.qrcode.model.Produto
import com.api.qrcode.model.QRCode
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import java.awt.print.Pageable
import java.sql.Timestamp
import java.util.Optional

interface QRCodeRepository: MongoRepository<QRCode, ObjectId>{

    fun findByCodigo(codigo: Int): Optional<QRCode>
    @Query("{ 'parceiro.cnpj':  ?0 }")
    fun findAllByParceiro(cnpj: String): List<QRCode>
    //@Query("{ 'produto._id' : ?0}")
    fun findAllByProduto(produto: Produto): List<QRCode>
}