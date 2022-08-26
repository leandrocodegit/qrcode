package com.api.qrcode.controller.response

import org.springframework.content.commons.annotations.ContentLength
import javax.persistence.Column

class ImagemReponse(
    var contentId: String,
    var link: String,
)