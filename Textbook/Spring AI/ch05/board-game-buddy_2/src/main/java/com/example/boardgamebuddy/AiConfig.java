package com.example.boardgamebuddy;

import com.example.boardgamebuddy.chatmemory.ConversationRepository;
import com.example.boardgamebuddy.chatmemory.MongoChatMemoryRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

  @Bean
  ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
    return MessageWindowChatMemory.builder()
        .chatMemoryRepository(chatMemoryRepository)
        .build();
  }

  @Bean
  ChatMemoryRepository chatMemoryRepository(ConversationRepository conversationRepository) {
    return new MongoChatMemoryRepository(conversationRepository);
  }

  @Bean
  ChatClient chatClient(
      ChatClient.Builder chatClientBuilder,
      VectorStore vectorStore,
      ChatMemory chatMemory) {

    return chatClientBuilder
        .defaultAdvisors(
            MessageChatMemoryAdvisor.builder(chatMemory)
                .build(),
            QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(SearchRequest.builder().build()).build())
        .build();
  }

}
