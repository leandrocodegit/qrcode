package com.api.qrcode.repository

import com.api.qrcode.model.Imagem
import org.springframework.content.commons.repository.ContentStore
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource
interface ImageStore: ContentStore<Imagem, String>