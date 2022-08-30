package com.api.qrcode.consumer

import com.api.qrcode.enuns.Status

interface ProcessaMensagem {

     fun processaUpdateStock(fromID: String)
     fun processaCreateEntity(fromID: String)
     fun processaUpdateEntity(fromID: String)
     fun processaDeleteEntity(fromID: String)
     fun processaStatusQRCode(fromID: String, status: Status)
    fun processaUpdatePrecoQRCode(codigo: String)
}