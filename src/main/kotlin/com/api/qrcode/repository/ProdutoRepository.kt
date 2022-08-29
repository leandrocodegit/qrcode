package com.api.qrcode.repository

import com.api.qrcode.model.Produto
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

interface ProdutoRepository: MongoRepository<Produto, String>{

    @Query("{ 'codigo' : ?0 }")
    fun findByCodigo(codigo: String): Optional<Produto>
}