package com.api.qrcode.configurations

import com.api.qrcode.events.ImageContentEventListener
import org.springframework.beans.factory.annotation.Value
import org.springframework.content.fs.io.FileSystemResourceLoader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File


@Configuration
class StoreConfig{

    @Value("\${dir.qrcode}")
    private lateinit var diretorio: String

    @Bean
    fun filesystemRoot(): File? {
        try {
            return File(diretorio)
        } catch (ioe: Exception) {
            ioe.printStackTrace()
        }
        return null
    }
    @Bean
    fun fileSystemResourceLoader(): FileSystemResourceLoader? {
        try {
            var file = File(filesystemRoot()?.absolutePath)
            if(file.exists().not())
                file.mkdir()
        }catch (ex: Exception){}
        return FileSystemResourceLoader(filesystemRoot()?.absolutePath)
    }
    @Bean
    fun imageContentEventListener(): ImageContentEventListener {
        return ImageContentEventListener()
    }

}