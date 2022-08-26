package com.api.qrcode.build

import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@ExtendWith(MockKExtension::class)
@AutoConfigureMockMvc
@EnableWebMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
annotation class ConfiguracaoPadraoController()
