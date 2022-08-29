package com.api.qrcode.repository

import com.api.qrcode.model.Parceiro
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import java.util.Optional

interface ParceiroRepository: MongoRepository<Parceiro, String> {

    @Query("{ 'cnpj' : ?0 }")
    fun findByCnpj(cnpj: String): Optional<Parceiro>
}