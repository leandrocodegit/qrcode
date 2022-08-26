package com.api.qrcode.controller

import com.api.qrcode.controller.request.QRCodeRequest
import com.api.qrcode.mapper.QRCodeMapper
import com.api.qrcode.service.QRCodeService
import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/qrcode")
class QRCodeController(
    private val qrCodeService: QRCodeService,
    private val mapper: QRCodeMapper
) {

    @GetMapping
    fun listatodosQRCode(@PageableDefault(size = 20) page: Pageable) =
        ResponseEntity.ok(qrCodeService.listaTodosQRCode(page).map { mapper.toResponse(it) })

    @GetMapping("/{id}")
    fun buscaQRcodePorId(@PathVariable id: String) =
        ResponseEntity.ok(mapper.toResponse(qrCodeService.buscaQRCode(id)))

    @PostMapping
    fun criarNovoQRCode() =
        ResponseEntity.ok(mapper.toResponse(qrCodeService.criarQRCode()))

    @PutMapping
    fun associarQRCode(@RequestBody request: QRCodeRequest) =
        ResponseEntity.ok(mapper.toResponse(qrCodeService.associarQRCode(request)))

    @DeleteMapping("/{id}")
    fun deleteQRCode(@PathVariable id: ObjectId){
        qrCodeService.deleteQRCode(id,false)
    }
}