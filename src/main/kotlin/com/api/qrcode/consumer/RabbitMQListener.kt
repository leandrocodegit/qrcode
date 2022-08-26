package com.api.qrcode.consumer

import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service
import org.springframework.amqp.core.Message

@Service
class RabbitMQListener( ){


    @RabbitListener(queues = ["QUEUE_NOTIFICACAO-PARCEIRO"])
    fun receiveParceiroMessage(message : Message){

        val body = message.body?.let { String(it) }
        println("PAR- $body")
    }

    @RabbitListener(queues = ["QUEUE_NOTIFICACAO-PRODUTO"])
    fun receiveProdutoMessage(message : Message){
        val body = message.body?.let { String(it) }
        println("PRO- $body")
    }

    @RabbitListener(queues = ["QUEUE_NOTIFICACAO-PRODUTO"])
    fun receiveProdutoMessageS(message : Message){
        val body = message.body?.let { String(it) }
        println("PRO- $body")
    }
}