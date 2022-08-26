package com.api.qrcode.controller.request

import com.api.qrcode.enuns.Status
import org.bson.types.ObjectId
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class QRCodeStatusRequest(
    @field:NotBlank
    var id: ObjectId,
    @field:NotNull
    var status: Status
)