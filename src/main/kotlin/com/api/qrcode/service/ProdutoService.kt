package com.api.qrcode.service

import com.api.qrcode.enuns.CodeError
import com.api.qrcode.exceptions.EntityResponseException
import com.api.qrcode.model.Produto
import com.api.qrcode.repository.ProdutoRepository
import org.springframework.stereotype.Service

@Service
class ProdutoService(
    private val produtoRepository: ProdutoRepository
) {
    fun buscaProdutoBycodigo(codigo: String) = produtoRepository.findById(codigo).orElseThrow {
        throw EntityResponseException("Produto nao encontrado", CodeError.NOT_FOUND)
    }

    fun createProduto(produto: Produto){
        if(produtoRepository.findByCodigo(produto.codigo).isEmpty)
            produtoRepository.save(produto)
        print("Criado")
    }

    fun atualizaProduto(produto: Produto): Produto{
      produto.codigo = buscaProdutoBycodigo(produto.codigo).codigo
          return  produtoRepository.save(produto)
    }

    fun deleteProduto(codigo: String){
        buscaProdutoBycodigo(codigo)
        produtoRepository.deleteById(codigo)
    }
}