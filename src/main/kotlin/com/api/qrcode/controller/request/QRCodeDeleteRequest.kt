package com.api.qrcode.controller.request

import org.bson.types.ObjectId
import java.sql.Timestamp

class QRCodeChangeRequest(
    var id: ObjectId,
    var timestamp: Int
)