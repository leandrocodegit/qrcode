package com.api.qrcode.rest

import com.api.qrcode.model.Parceiro
import com.api.qrcode.model.Produto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(
    name = "restParceiro",
    url = "\${integration.parceiro.url}"
)
interface RestParceiro {

    @GetMapping("/api/v1/parceiro/{cnpj}")
    fun getParceiro(@PathVariable cnpj: String): Parceiro
}