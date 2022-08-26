package com.api.qrcode.build

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.google.gson.Gson
import org.springframework.cloud.contract.spec.internal.HttpStatus.OK
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

class QRCodeComponent {

    companion object: AbstractTest(){

        fun createMockBookProduto(status: HttpStatus) {
            stubFor(
                get(urlPathEqualTo("/api/v1/produto/${QRCodeBuild.produto.codigo}}"))
                    .willReturn(
                        aResponse()
                            .withStatus(status.value())
                            .withBody(Gson().toJson(QRCodeBuild.produto))
                            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    )
            )
        }


    }
}