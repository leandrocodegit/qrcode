package com.api.qrcode.exceptions

import com.api.qrcode.enuns.CodeError
import feign.Response
import feign.codec.ErrorDecoder

class CustomErrorDecoder: ErrorDecoder {
    override fun decode(methodKey: String, response: Response): Exception {
        return when (response.status()) {
            400 -> throw EntityResponseException("Response erro 400",CodeError.REST_ERROR)
            403 -> throw EntityResponseException("Response erro 403",CodeError.REST_ERROR)
            404 -> throw EntityResponseException("Response erro 404",CodeError.REST_ERROR)
            else -> throw EntityResponseException("Error interno",CodeError.REST_ERROR)
        }

    }

}