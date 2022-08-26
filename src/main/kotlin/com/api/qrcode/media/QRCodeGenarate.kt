package com.api.qrcode.media

import com.api.qrcode.model.Imagem
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.content.fs.io.FileSystemResourceLoader
import org.springframework.stereotype.Component
import java.awt.*
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.ImageIO
import kotlin.io.path.Path

@Component
class QRCodeGenarate {

    @Autowired
    private lateinit var pathRoot: FileSystemResourceLoader

    fun gerarQR(id: String, cogigo: Int):Imagem {
       var imagem = Imagem(
            UUID.randomUUID().toString(),
            0,
            "image/png")
        val size = 250
        val fileType = "png"
        val myFile = File("${pathRoot.rootResource.path}${imagem.contentId}")
        try {
            val hintMap: MutableMap<EncodeHintType, Any> = EnumMap<EncodeHintType, Any>(
                EncodeHintType::class.java
            )
            hintMap[EncodeHintType.CHARACTER_SET] = "UTF-8"

            hintMap[EncodeHintType.MARGIN] = 1
            hintMap[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.L
            val qrCodeWriter = QRCodeWriter()
            val byteMatrix: BitMatrix = qrCodeWriter.encode(
                "https://decoratem.com.br:4200/${id}", BarcodeFormat.QR_CODE, size,
                size, hintMap
            )
            val width: Int = byteMatrix.width
            val image = BufferedImage(
                width, width + 20,
                BufferedImage.TYPE_INT_RGB
            )

            val graphics = image.graphics as Graphics2D
            graphics.color = Color.WHITE
            graphics.fillRect(0, 0, width, width + 20);
            graphics.color = Color.GRAY
            graphics.font = Font("Arial", Font.PLAIN, 15)
            graphics.drawString("C$cogigo", 8, 260)
            graphics.color = Color.BLACK
            for (i in 0 until width) {
                for (j in 0 until width) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1 , 1 )
                    }
                }
            }
            ImageIO.write(image, fileType, myFile)
            imagem.contentLength = myFile.length()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return imagem
    }

}
