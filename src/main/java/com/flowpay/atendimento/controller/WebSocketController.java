package com.flowpay.atendimento.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class WebSocketController {

    @MessageMapping("/ping")
    @SendTo("/topic/pong")
    public String handlePing(String message) {
        log.info("Recebido ping: {}", message);
        return "pong: " + message;
    }
}
