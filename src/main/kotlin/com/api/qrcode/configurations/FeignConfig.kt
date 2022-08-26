package com.api.qrcode.configurations

import com.api.qrcode.exceptions.CustomErrorDecoder
import feign.codec.ErrorDecoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class FeignConfig {

    @Bean
    fun errorDecoder(): ErrorDecoder {
        return CustomErrorDecoder()
    }
}