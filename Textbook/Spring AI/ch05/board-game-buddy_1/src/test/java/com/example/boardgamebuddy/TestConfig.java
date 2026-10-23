package com.example.boardgamebuddy;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {

  @Bean
  ChatMemory chatMemory() {
    return MessageWindowChatMemory.builder().build();
  }

}
