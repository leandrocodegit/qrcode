package com.api.qrcode.events

import com.api.qrcode.model.Imagem
import org.springframework.content.commons.annotations.HandleAfterSetContent
import org.springframework.content.commons.annotations.StoreEventHandler
import java.io.File
import kotlin.io.path.Path

@StoreEventHandler
class ImageContentEventListener {


    @HandleAfterSetContent
    fun handleAfterSetContent(doc: Imagem) {
        deleteFile(doc.contentId)
    }

    @HandleAfterSetContent
    fun onAfterSetContent(doc: Imagem) {
        //deleteFile(doc.contentId)
    }

    private fun deleteFile(id: String) {
   println("Deletando $id ${File("${Path("").toAbsolutePath().toString()}/qrcode/${id}.png").exists()}")
        try {
            val path = "${Path("").toAbsolutePath().toString()}\\qrcode\\${id}.png"
            val file = File(path)
            if (file.exists()) {
                println("Deletando...")
              //  Thread.sleep(3000)
                file.delete()
                println("Deletado")

            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}
