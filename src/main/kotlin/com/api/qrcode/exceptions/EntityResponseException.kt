package com.api.qrcode.exceptions

import com.api.qrcode.enuns.CodeError

class EntityResponseException(message: String, var codeError: CodeError): RuntimeException(message)