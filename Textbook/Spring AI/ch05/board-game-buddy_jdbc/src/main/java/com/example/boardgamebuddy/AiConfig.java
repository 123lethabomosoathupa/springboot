package com.example.boardgamebuddy;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.memory.repository.jdbc.PostgresChatMemoryRepositoryDialect;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class AiConfig {

  
  @Bean
  ChatMemoryRepository chatMemoryRepository(DataSource dataSource) {
    return JdbcChatMemoryRepository.builder()
        .dialect(new PostgresChatMemoryRepositoryDialect())
        .dataSource(dataSource)
        .build();
  }
  

  @Bean
  ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
    return MessageWindowChatMemory.builder()
        .chatMemoryRepository(chatMemoryRepository)
        .build();
  }

  @Bean
  ChatClient chatClient(
          ChatClient.Builder chatClientBuilder, 
          VectorStore vectorStore,
          ChatMemory chatMemory) {
    return chatClientBuilder
        .defaultAdvisors(
            MessageChatMemoryAdvisor.builder(chatMemory).build(),
            QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(SearchRequest.builder().build()).build())
          .build();
  }

}
