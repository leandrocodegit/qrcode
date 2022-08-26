package com.api.qrcode.exceptions
import java.sql.Timestamp
import java.time.Instant

class ResponseError(
        var message: String?,
        var type: String?,
        var codigo: String,
        var erros: List<String>) {
    var containsError: Boolean = true
    var timestamp: Timestamp = Timestamp.from(Instant.now())
}