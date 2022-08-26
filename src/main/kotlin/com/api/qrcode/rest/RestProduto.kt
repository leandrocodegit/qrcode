package com.api.qrcode.rest

import com.api.qrcode.enuns.CodeError
import com.api.qrcode.exceptions.EntityResponseException
import com.api.qrcode.model.Parceiro
import com.api.qrcode.model.Produto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable


@FeignClient(
    name = "restProduto",
    url = "\${integration.produto.url}"
)
interface RestProduto {

    @GetMapping("/api/v1/produto/{codigo}")
    fun getProduto(@PathVariable codigo: String): Produto
    @GetMapping("/api/v1/produto/preco/{codigo}")
    fun getPrecoProduto(@PathVariable codigo: String): Double

}

