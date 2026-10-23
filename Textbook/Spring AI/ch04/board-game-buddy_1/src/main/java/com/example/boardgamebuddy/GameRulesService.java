package com.example.boardgamebuddy;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameRulesService {

  private final VectorStore vectorStore;

  public GameRulesService(VectorStore vectorStore) {
    this.vectorStore = vectorStore;
  }

  public String getRulesFor(String gameName, String question) {
    var searchRequest = SearchRequest
        .builder()
        .query(question)
        .filterExpression(
            new FilterExpressionBuilder()
                .eq("gameTitle", normalizeGameTitle(gameName)).build())
        .build(); 

    System.err.println("Search request: " + searchRequest);

    var similarDocs =
        vectorStore.similaritySearch(searchRequest); 

    if (similarDocs.isEmpty()) {
      return "The rules for " + gameName + " are not available.";
    }

    return similarDocs.stream()
        .map(Document::getText)
        .collect(Collectors.joining(System.lineSeparator())); 
  }

  private String normalizeGameTitle(String gameTitle) {  
    return gameTitle.toLowerCase().replace(" ", "_");
  }

}
