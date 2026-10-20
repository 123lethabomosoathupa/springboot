
package com.example.boardgamebuddy;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;

import java.util.List;


/*

@Service

*/

public class SelfEvaluatingBoardGameService implements BoardGameService {

  private final ChatClient chatClient;
  private final RelevancyEvaluator evaluator;

  public SelfEvaluatingBoardGameService(ChatClient.Builder chatClientBuilder) {
    var chatOptions = ChatOptions.builder()
        .model("gpt-4o-mini")
        .build();

    this.chatClient = chatClientBuilder
        .defaultOptions(chatOptions)
        .build();

    this.evaluator = new RelevancyEvaluator(chatClientBuilder); 
  }

  @Override
  @Retryable(retryFor = AnswerNotRelevantException.class)  
  public Answer askQuestion(Question question) {
    var answerText = chatClient.prompt()
        .user(question.question())
        .call()
        .content();

    evaluateRelevancy(question, answerText);

    return new Answer(answerText);
  }

  @Recover 
  public Answer recover(AnswerNotRelevantException e) {
    return new Answer("I'm sorry, I wasn't able to answer the question.");
  }

  private void evaluateRelevancy(Question question, String answerText) {
    var evaluationRequest =
        new EvaluationRequest(question.question(), answerText);
    var evaluationResponse = evaluator.evaluate(evaluationRequest);
    if (!evaluationResponse.isPass()) {
      throw new AnswerNotRelevantException(question.question(), answerText); 
    }
  }

}

