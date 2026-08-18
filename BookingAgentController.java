package com.example.booking.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookingAgentController {

    private final ChatClient chatClient;

    public BookingAgentController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

    @GetMapping("/api/chat")
    public String chat(@RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .functions("getRoomAvailability", "calculateTotalPrice")
                .call()
                .content();
    }
}