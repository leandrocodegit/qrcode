package com.api.qrcode.controller

import com.api.qrcode.repository.ImageStore
import com.api.qrcode.service.QRCodeService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/qrcode/imagem")
class ImagemController(
    private val imageStore: ImageStore
) {

    @GetMapping("{id}")
    fun fileImagem(@PathVariable id: String): ResponseEntity<ByteArray> {
        if (imageStore.getResource(id).file.exists().not())
            ResponseEntity.badRequest()
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageStore.getResource(id).file.readBytes());
    }
}