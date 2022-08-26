package com.api.qrcode.service

import com.api.qrcode.build.QRCodeBuild
import com.api.qrcode.controller.request.QRCodeRequest
import com.api.qrcode.controller.request.QRCodeStatusRequest
import com.api.qrcode.enuns.CodeError
import com.api.qrcode.enuns.Status
import com.api.qrcode.exceptions.EntityResponseException
import com.api.qrcode.media.QRCodeGenarate
import com.api.qrcode.model.Estoque
import com.api.qrcode.model.Imagem
import com.api.qrcode.model.Produto
import com.api.qrcode.model.QRCode
import com.api.qrcode.repository.ImageStore
import com.api.qrcode.repository.QRCodeRepository
import com.api.qrcode.rest.RestParceiro
import com.api.qrcode.rest.RestProduto
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.util.*

@ExtendWith(MockKExtension::class)
@EnableAutoConfiguration
class QRCodeServiceTest {

    @InjectMockKs
    private lateinit var qrCodeService: QRCodeService
    @MockK
    private lateinit var qrCodeRepository: QRCodeRepository
    @MockK
    private lateinit var restProduto: RestProduto
    @MockK
    private lateinit var restParceiro: RestParceiro
    @MockK
    private lateinit var imageStore: ImageStore
    @MockK
    private lateinit var qrCodeGenarate: QRCodeGenarate

    @Test
    fun `test listar todos qrcodes`(){

        val pages : Page<QRCode> = PageImpl<QRCode>(listOf(QRCodeBuild.qrcode(ObjectId.get()),QRCodeBuild.qrcode(ObjectId.get())))
        every { qrCodeRepository.findAll(PageRequest.of(0,2, Sort.unsorted())) } returns pages

        val lista = qrCodeService.listaTodosQRCode(PageRequest.of(0,2, Sort.unsorted()))

        assertEquals(2,lista.size)

    }

    @Test
    fun `test criar novo qrcode`(){

        var qrcode = QRCodeBuild.qrcode()
        qrcode.parceiro!!.comissao = 10.0

        every { qrCodeGenarate.gerarQR(any(),any()) } returns Imagem(
            UUID.fromString("d2c2a587-be9b-4423-ba03-742872aa8654").toString(),
            10,
            "image/png")
        every { qrCodeRepository.save(any()) } returns qrcode
        every { restProduto.getProduto(qrcode.produto!!.codigo) } returns Produto("7000","Jogo de 6 taças", 100.0, Estoque(1L,10,0),
            "",
            "",true)
        every { restParceiro.getParceiro(qrcode.parceiro!!.cnpj) } returns qrcode.parceiro!!

        val novoQrcode = qrCodeService.criarQRCode()

        assertEquals(novoQrcode.desconto, 0.0)
        assertEquals(novoQrcode.status, Status.INATIVO)
        assertEquals(novoQrcode.preco, 0.0 )
        assertNotNull(novoQrcode.imagem)

        verify (exactly = 1) {  qrCodeRepository.save(any()) }

    }

    @Test
    fun `test associar qrcode`(){

        var qrcode = QRCodeBuild.qrcode()
        qrcode.parceiro!!.comissao = 10.0
        qrcode.parceiro!!.status = Status.ATIVO
        qrcode.parceiro!!.cnpj = "123"

        every { qrCodeRepository.findById(qrcode.id) } returns Optional.of(qrcode)
        every { restProduto.getProduto(qrcode.produto!!.codigo) } returns Produto("7000","Jogo de 6 taças", 100.0, Estoque(1L,10,0),
            "",
            "",true)
        every { restParceiro.getParceiro(qrcode.parceiro!!.cnpj) } returns qrcode.parceiro!!
        every { qrCodeRepository.save(qrcode) } returns qrcode

        val saveQRCode = qrCodeService.associarQRCode(QRCodeRequest(qrcode.id.toString(),qrcode.produto!!.codigo,qrcode.parceiro!!.cnpj))

        assertNotNull(saveQRCode.parceiro)
        assertNotNull(saveQRCode.produto)
        assertEquals(110.0,saveQRCode.preco)

        verify (exactly = 1) {  qrCodeRepository.findById(qrcode.id)  }
        verify (exactly = 1) {  restProduto.getProduto(qrcode.produto!!.codigo)  }
        verify (exactly = 1) {  restParceiro.getParceiro(qrcode.parceiro!!.cnpj) }
    }

    @Test
    fun `test associar qrcode parceiro inativo`(){

        var qrcode = QRCodeBuild.qrcode()
        qrcode.parceiro!!.comissao = 10.0
        qrcode.parceiro!!.status = Status.INATIVO

        every { qrCodeRepository.findById(qrcode.id) } returns Optional.of(qrcode)
        every { restProduto.getProduto(qrcode.produto!!.codigo) } returns Produto("7000","Jogo de 6 taças", 100.0, Estoque(1L,10,0),
            "",
            "",true)
        every { restParceiro.getParceiro(qrcode.parceiro!!.cnpj) } returns qrcode.parceiro!!

        assertThrows<EntityResponseException> { qrCodeService.associarQRCode(QRCodeRequest(qrcode.id.toString(),qrcode.produto!!.codigo,qrcode.parceiro!!.cnpj)) }

        verify (exactly = 1) {  qrCodeRepository.findById(qrcode.id)  }
        verify (exactly = 1) {  restProduto.getProduto(qrcode.produto!!.codigo)  }
        verify (exactly = 1) {  restParceiro.getParceiro(qrcode.parceiro!!.cnpj) }
    }

    @Test
    fun `test associar qrcode produto inativo`(){

        var qrcode = QRCodeBuild.qrcode()
        qrcode.parceiro!!.comissao = 10.0
        qrcode.parceiro!!.status = Status.ATIVO

        every { qrCodeRepository.findById(qrcode.id) } returns Optional.of(qrcode)
        every { restProduto.getProduto(qrcode.produto!!.codigo) } returns Produto("7000","Jogo de 6 taças", 100.0, Estoque(1L,10,0),
            "",
            "",false)
        every { restParceiro.getParceiro(qrcode.parceiro!!.cnpj) } returns qrcode.parceiro!!

        assertThrows<EntityResponseException> { qrCodeService.associarQRCode(QRCodeRequest(qrcode.id.toString(),qrcode.produto!!.codigo,qrcode.parceiro!!.cnpj)) }

        verify (exactly = 1) {  qrCodeRepository.findById(qrcode.id)  }
        verify (exactly = 1) {  restProduto.getProduto(qrcode.produto!!.codigo)  }
        verify (exactly = 1) {  restParceiro.getParceiro(qrcode.parceiro!!.cnpj) }
    }

    @Test
    fun `test ativa qrcode cadastrado`(){

        var qrcode = QRCodeBuild.qrcode()
        qrcode.status = Status.INATIVO
        qrcode.parceiro!!.status = Status.ATIVO
        qrcode.parceiro!!.cnpj = "123"

        every { restProduto.getProduto(qrcode.produto!!.codigo) } returns Produto("7000","Jogo de 6 taças", 100.0, Estoque(1L,10,0),
            "",
            "",true)
        every { restParceiro.getParceiro(qrcode.parceiro!!.cnpj) } returns qrcode.parceiro!!
        every { qrCodeRepository.save(qrcode) } returns qrcode
        every { qrCodeRepository.findById(qrcode.id) } returns Optional.of(qrcode)

        val saveQRCode = qrCodeService.atualizaStatusQRCode(QRCodeStatusRequest(qrcode.id, Status.ATIVO))

        assertEquals(Status.ATIVO, saveQRCode.status)

        verify (exactly = 1) {  qrCodeRepository.findById(qrcode.id)  }
        verify (exactly = 1) { qrCodeRepository.save(qrcode)  }
    }

    @Test
    fun `test inativa qrcode cadastrado`(){

        var qrcode = QRCodeBuild.qrcode()
        qrcode.status = Status.ATIVO
        qrcode.parceiro!!.status = Status.INATIVO
        qrcode.parceiro!!.cnpj = "123"

        every { restProduto.getProduto(qrcode.produto!!.codigo) } returns Produto("7000","Jogo de 6 taças", 100.0, Estoque(1L,10,0),
            "",
            "",false)
        every { restParceiro.getParceiro(qrcode.parceiro!!.cnpj) } returns qrcode.parceiro!!
        every { qrCodeRepository.save(qrcode) } returns qrcode
        every { qrCodeRepository.findById(qrcode.id) } returns Optional.of(qrcode)

        val saveQRCode = qrCodeService.atualizaStatusQRCode(QRCodeStatusRequest(qrcode.id, Status.INATIVO))

        assertEquals(Status.INATIVO, saveQRCode.status)

        verify (exactly = 0) {  restParceiro.getParceiro(qrcode.parceiro!!.cnpj)  }
        verify (exactly = 0) { restProduto.getProduto(qrcode.produto!!.codigo)   }
        verify (exactly = 1) {  qrCodeRepository.findById(qrcode.id)  }
        verify (exactly = 1) { qrCodeRepository.save(qrcode)  }
    }

    @Test
    fun `test ativa qrcode cadastrado produto inativo`(){

        var qrcode = QRCodeBuild.qrcode()
        qrcode.status = Status.INATIVO
        qrcode.parceiro!!.status = Status.ATIVO
        qrcode.parceiro!!.cnpj = "123"

        every { restProduto.getProduto(qrcode.produto!!.codigo) } returns Produto("7000","Jogo de 6 taças", 100.0, Estoque(1L,10,0),
            "",
            "",false)
        every { restParceiro.getParceiro(qrcode.parceiro!!.cnpj) } returns qrcode.parceiro!!
        every { qrCodeRepository.save(qrcode) } returns qrcode
        every { qrCodeRepository.findById(qrcode.id) } returns Optional.of(qrcode)

        assertThrows<EntityResponseException> {qrCodeService.atualizaStatusQRCode(QRCodeStatusRequest(qrcode.id, Status.ATIVO))}

        verify (exactly = 1) {  qrCodeRepository.findById(qrcode.id)  }
        verify (exactly = 0) { qrCodeRepository.save(qrcode)  }
    }

    @Test
    fun `test ativa qrcode cadastrado paceiro inativo`(){

        var qrcode = QRCodeBuild.qrcode()
        qrcode.status = Status.INATIVO
        qrcode.parceiro!!.status = Status.INATIVO
        qrcode.parceiro!!.cnpj = "123"

        every { restProduto.getProduto(qrcode.produto!!.codigo) } returns Produto("7000","Jogo de 6 taças", 100.0, Estoque(1L,10,0),
            "",
            "",true)
        every { restParceiro.getParceiro(qrcode.parceiro!!.cnpj) } returns qrcode.parceiro!!
        every { qrCodeRepository.save(qrcode) } returns qrcode
        every { qrCodeRepository.findById(qrcode.id) } returns Optional.of(qrcode)

        assertThrows<EntityResponseException> {qrCodeService.atualizaStatusQRCode(QRCodeStatusRequest(qrcode.id, Status.ATIVO))}

        verify (exactly = 1) {  qrCodeRepository.findById(qrcode.id)  }
        verify (exactly = 0) { qrCodeRepository.save(qrcode)  }
    }

    @Test
    fun `test atualiza status qrcode nao cadastrado`(){

        var qrcode = QRCodeBuild.qrcode()

        every { qrCodeRepository.save(qrcode) } returns qrcode
        every { qrCodeRepository.findById(qrcode.id) } returns Optional.empty()

        assertThrows<EntityResponseException> {qrCodeService.atualizaStatusQRCode(QRCodeStatusRequest(qrcode.id, Status.ATIVO))}

        verify (exactly = 1) {  qrCodeRepository.findById(qrcode.id)  }
    }

    @Test
    fun `test ativa qrcode cadastrado paceiro nulo`(){

        var qrcode = QRCodeBuild.qrcode()
        qrcode.status = Status.INATIVO
        qrcode.parceiro = null

        every { qrCodeRepository.findById(qrcode.id) } returns Optional.of(qrcode)

        assertThrows<EntityResponseException> {qrCodeService.atualizaStatusQRCode(QRCodeStatusRequest(qrcode.id, Status.ATIVO))}

        verify (exactly = 1) {  qrCodeRepository.findById(qrcode.id)  }
    }

    @Test
    fun `test ativa qrcode cadastrado produto nulo`(){

        var qrcode = QRCodeBuild.qrcode()
        qrcode.status = Status.INATIVO
        qrcode.produto = null

        every { qrCodeRepository.findById(qrcode.id) } returns Optional.of(qrcode)

        assertThrows<EntityResponseException> {qrCodeService.atualizaStatusQRCode(QRCodeStatusRequest(qrcode.id, Status.ATIVO))}

        verify (exactly = 1) {  qrCodeRepository.findById(qrcode.id)  }
    }

    @Test
    fun `test delete qrcode cadastrado nao impresso`(){

        var qrcode = QRCodeBuild.qrcode()

        every { qrCodeRepository.findById(qrcode.id) } returns Optional.of(qrcode)
        every { qrCodeRepository.deleteById(qrcode.id) } returns Unit

        assertEquals(Unit, qrCodeService.deleteQRCode(qrcode.id,false))

        verify (exactly = 1) { qrCodeRepository.deleteById(qrcode.id)   }
        verify (exactly = 1) {  qrCodeRepository.findById(qrcode.id)  }
    }



    @Test
    fun `test delete qrcode nao cadastrado`(){

        var qrcode = QRCodeBuild.qrcode()

        every { qrCodeRepository.findById(qrcode.id) } returns Optional.empty()
        every { qrCodeRepository.deleteById(qrcode.id) } returns Unit

        assertThrows<EntityResponseException> {qrCodeService.deleteQRCode(qrcode.id,false)}

        verify (exactly = 0) { qrCodeRepository.deleteById(qrcode.id)   }
        verify (exactly = 1) {  qrCodeRepository.findById(qrcode.id)  }
    }

    @Test
    fun `test delete qrcode cadastrado ja impresso nao forcado`(){

        var qrcode = QRCodeBuild.qrcode()
        qrcode.isImpresso = true

        every { qrCodeRepository.findById(qrcode.id) } returns Optional.of(qrcode)
        every { qrCodeRepository.deleteById(qrcode.id) } returns Unit

        assertThrows<EntityResponseException> {qrCodeService.deleteQRCode(qrcode.id,false)}

        verify (exactly = 0) { qrCodeRepository.deleteById(qrcode.id)   }
        verify (exactly = 1) {  qrCodeRepository.findById(qrcode.id)  }
    }

    @Test
    fun `test delete qrcode cadastrado impresso forcado`(){

        var qrcode = QRCodeBuild.qrcode()
        qrcode.isImpresso = true

        every { qrCodeRepository.findById(qrcode.id) } returns Optional.of(qrcode)
        every { qrCodeRepository.deleteById(qrcode.id) } returns Unit

        assertEquals(Unit, qrCodeService.deleteQRCode(qrcode.id,true))

        verify (exactly = 1) { qrCodeRepository.deleteById(qrcode.id)   }
        verify (exactly = 1) {  qrCodeRepository.findById(qrcode.id)  }
    }

    @Test
    fun `test marca qrcode cadastrado como impresso`(){

        var qrcode = QRCodeBuild.qrcode()
        qrcode.isImpresso = false

        every { qrCodeRepository.findById(qrcode.id) } returns Optional.of(qrcode)
        every { qrCodeRepository.save(any()) } returns qrcode

        assertEquals(true, qrCodeService.marqueImpresso(qrcode.id,true))

        verify (exactly = 1) { qrCodeRepository.save(any()) }
        verify (exactly = 1) {  qrCodeRepository.findById(qrcode.id)  }
    }

    @Test
    fun `test marca qrcode nao cadastrado como impresso`(){

        var qrcode = QRCodeBuild.qrcode()
        qrcode.isImpresso = false

        every { qrCodeRepository.findById(qrcode.id) } returns Optional.empty()
        every { qrCodeRepository.save(any()) } returns qrcode

        assertThrows<EntityResponseException> {qrCodeService.marqueImpresso(qrcode.id,true)}

        verify (exactly = 0) { qrCodeRepository.save(any()) }
        verify (exactly = 1) {  qrCodeRepository.findById(qrcode.id)  }
    }

    @Test
    fun `test marca qrcode cadastrado como nao impresso`(){

        var qrcode = QRCodeBuild.qrcode()
        qrcode.isImpresso = true

        every { qrCodeRepository.findById(qrcode.id) } returns Optional.of(qrcode)
        every { qrCodeRepository.save(any()) } returns qrcode

        assertEquals(false, qrCodeService.marqueImpresso(qrcode.id,false))

        verify (exactly = 1) { qrCodeRepository.save(any()) }
        verify (exactly = 1) {  qrCodeRepository.findById(qrcode.id)  }
    }


}