package com.api.qrcode.model

import org.springframework.content.commons.annotations.ContentId
import org.springframework.content.commons.annotations.ContentLength
import org.springframework.data.mongodb.core.mapping.Document
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Document
class Imagem(
    @Id
    @ContentId
    @Column(name = "content_id")
    var contentId: String,
    @ContentLength
    @Column(name = "content_length")
    var contentLength: Long,
    @Column(name = "content_mime_type")
    var contentMimeType: String,
){
    @Transient
    var link: String = ""
        get() {
            return "/api/v1/imagem/$contentId"
        }

}