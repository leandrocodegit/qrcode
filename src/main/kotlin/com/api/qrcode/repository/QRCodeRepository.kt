package com.api.qrcode.repository

import com.api.qrcode.model.QRCode
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.mongodb.repository.MongoRepository
import java.awt.print.Pageable
import java.sql.Timestamp
import java.util.Optional

interface QRCodeRepository: MongoRepository<QRCode, ObjectId>{

    fun findByCodigo(codigo: Int): Optional<QRCode>
}