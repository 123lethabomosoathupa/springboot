
package com.example.boardgamebuddy;

import org.springframework.ai.chat.client.ChatClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import org.springframework.stereotype.Service;

@Service
public class SpringAiBoardGameService implements BoardGameService {

  private final ChatClient chatClient;

  public SpringAiBoardGameService(ChatClient.Builder chatClientBuilder) { 
    this.chatClient = chatClientBuilder.build(); 
  }

  

  /*
  
  private static final String questionPromptTemplate = """ 
      Answer this question about {game}: {question}
      """;
  
  */

  
  @Value("classpath:/promptTemplates/questionPromptTemplate.st")
  Resource questionPromptTemplate;
  
  

  @Override
  public Answer askQuestion(Question question) {
    var answerText = chatClient.prompt()
        .user(userSpec -> userSpec
            .text(questionPromptTemplate)      
            .param("gameTitle", question.gameTitle())
            .param("question", question.question()))   
        .call()
        .content();     

    return new Answer(question.gameTitle(), answerText);
  }

}

