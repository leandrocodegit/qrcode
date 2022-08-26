package com.api.qrcode.controller

import com.api.qrcode.build.ConfiguracaoPadraoController
import com.api.qrcode.build.QRCodeBuild
import com.api.qrcode.build.URL_QRCODE
import com.api.qrcode.build.URL_QRCODE_ID
import com.api.qrcode.media.QRCodeGenarate
import com.api.qrcode.model.QRCode
import com.api.qrcode.repository.ImageStore
import com.api.qrcode.repository.QRCodeRepository
import com.api.qrcode.rest.RestParceiro
import com.api.qrcode.rest.RestProduto
import com.api.qrcode.service.QRCodeService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@ConfiguracaoPadraoController
class QRCodeControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc
    @Autowired
    private lateinit var qrCodeService: QRCodeService
    @MockkBean
    private lateinit var qrCodeRepository: QRCodeRepository
    @MockkBean
    private lateinit var restProduto: RestProduto
    @MockkBean
    private lateinit var restParceiro: RestParceiro
    @MockkBean
    private lateinit var imageStore: ImageStore
    @MockkBean
    private lateinit var qrCodeGenarate: QRCodeGenarate


    @Test
    fun `test list todos qrcodes`(){

        val qrcode1 =  QRCodeBuild.qrcode(ObjectId.get())
        val qrcode2 =  QRCodeBuild.qrcode(ObjectId.get())

        val pages : Page<QRCode> = PageImpl<QRCode>(
            listOf(qrcode1,qrcode2))
        every { qrCodeRepository.findAll(any<Pageable>()) } returns pages

        mockMvc.perform(get(URL_QRCODE)
            .contentType(APPLICATION_JSON)
            .param("page","1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content.[0].id").value(qrcode1.id.toString()))
            .andExpect(jsonPath("$.content.[1].id").value(qrcode2.id.toString()))

    }

    @Test
    fun `test busca qrcode por id`(){

        val qrcode =  QRCodeBuild.qrcode(ObjectId.get())

        every { qrCodeRepository.findById(qrcode.id) } returns Optional.of(qrcode)

        mockMvc.perform(get(URL_QRCODE_ID, qrcode.id.toString())
            .contentType(APPLICATION_JSON) )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(qrcode.id.toString()))

    }

    @Test
    fun `test busca qrcode por id inexistente`(){

        val qrcode =  QRCodeBuild.qrcode(ObjectId.get())

        every { qrCodeRepository.findById(qrcode.id) } returns Optional.empty()

        mockMvc.perform(get(URL_QRCODE_ID, qrcode.id.toString())
            .contentType(APPLICATION_JSON) )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `test busca qrcode por codigo`(){

        val qrcode =  QRCodeBuild.qrcode(ObjectId.get())

        every { qrCodeRepository.findById(qrcode.id) } returns Optional.of(qrcode)
        every { qrCodeRepository.findByCodigo(qrcode.codigo) } returns Optional.of(qrcode)

        mockMvc.perform(get(URL_QRCODE_ID, qrcode.codigo)
            .contentType(APPLICATION_JSON) )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(qrcode.id.toString()))

        verify (exactly = 0) { qrCodeRepository.findById(qrcode.id) }
        verify (exactly = 1) { qrCodeRepository.findByCodigo(qrcode.codigo) }

    }

    @Test
    fun `test busca qrcode por codigo inexistente`(){

        val qrcode =  QRCodeBuild.qrcode(ObjectId.get())

        every { qrCodeRepository.findByCodigo(qrcode.codigo) } returns Optional.empty()

        mockMvc.perform(get(URL_QRCODE_ID, qrcode.codigo)
            .contentType(APPLICATION_JSON) )
            .andExpect(status().isBadRequest)
    }
}