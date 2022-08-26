package com.api.qrcode.mapper

import com.api.qrcode.controller.response.ProdutoResponse
import com.api.qrcode.controller.response.QRCodeResponse
import com.api.qrcode.model.Produto
import com.api.qrcode.model.QRCode
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper
interface QRCodeMapper {

    @Mapping(target = "id", expression = "java(qrcode.getId().toString())")
    fun toResponse(qrcode: QRCode): QRCodeResponse
    fun toResponse(produto: Produto): ProdutoResponse

}