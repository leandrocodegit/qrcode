package com.api.qrcode.configurations

import com.api.qrcode.events.ImageContentEventListener
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.content.fs.io.FileSystemResourceLoader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File



@SpringBootTest
class StoreConfig{

    @Bean
    fun filesystemRoot(): File? {
        try {
            return File("test")
        } catch (ioe: Exception) {
            ioe.printStackTrace()
        }
        return null
    }
    @Bean
    fun fileSystemResourceLoader(): FileSystemResourceLoader? {
        return FileSystemResourceLoader(filesystemRoot()?.absolutePath)
    }
    @Bean
    fun imageContentEventListener(): ImageContentEventListener {
        return ImageContentEventListener()
    }

}