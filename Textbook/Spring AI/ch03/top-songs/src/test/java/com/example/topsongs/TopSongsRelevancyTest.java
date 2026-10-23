package com.example.topsongs;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class TopSongsRelevancyTest {

  @Autowired
  private TopSongsController topSongsController;

  @Autowired
  private ChatClient.Builder chatClientBuilder;

  @Test
  public void evaluateRelevancy() {
    var results = topSongsController.topSongs("1984");
    var relevancyEvaluator = new RelevancyEvaluator(chatClientBuilder);

    var userText = "What were the top 10 songs on the Billboard Hot 100 in 1984?";

    var resultsString = String.join("\n", results);


    var response = relevancyEvaluator.evaluate(new EvaluationRequest(userText, List.of(), resultsString));
    Assertions.assertThat(response.isPass())
        .withFailMessage("""
          ========================================
          The answer "%s"
          is not considered relevant to the question
          "%s".
          ========================================
          """, resultsString, userText)
        .isTrue();
  }

}
