package com.api.qrcode.build

import com.github.tomakehurst.wiremock.WireMockServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class AbstractTest {

    @Value("\${integration.client.url}")
    lateinit var url: String

    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var wireMockServer: WireMockServer

}