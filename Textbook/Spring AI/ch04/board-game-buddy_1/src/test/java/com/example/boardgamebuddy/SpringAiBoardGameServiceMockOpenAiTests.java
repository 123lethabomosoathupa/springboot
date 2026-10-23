package com.example.boardgamebuddy;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.stringtemplate.v4.ST;

import java.nio.charset.Charset;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(components = {
    SpringAiBoardGameService.class,
    GameRulesService.class})
public class SpringAiBoardGameServiceMockOpenAiTests {

    @Autowired
    MockRestServiceServer mockServer;

    @Autowired
    SpringAiBoardGameService service;

    @MockitoBean
    VectorStore vectorStore;

    @TestConfiguration
    public static class TestConfig {
        @Bean
        public ChatClient.Builder chatClientBuilder(RestClient.Builder restClientBuilder, WebClient.Builder webClientBuilder) {
            var openAiChatModel = OpenAiChatModel.builder()
              .openAiApi(OpenAiApi.builder()
                  .apiKey("TEST_API_KEY")
                  .restClientBuilder(restClientBuilder)
                  .webClientBuilder(webClientBuilder)
                  .build())
              .build();
            return ChatClient.builder(openAiChatModel);
        }
    }

    @Test
    public void testStuff() throws Exception {
        var expectedAnswer = "Checkers is a game for two people.";
        var content = "Checkers is a game for two people.";
        mockOpenAiChatResponse(content);
        var answer = service.askQuestion(new Question("Checkers","How many can play?"));
        Assertions.assertThat(answer.answer()).isEqualTo(expectedAnswer);
    }

    private void mockOpenAiChatResponse(String content) throws Exception {
        var responseResource = new ClassPathResource("/response.json");
        var st = new ST(StreamUtils.copyToString(responseResource.getInputStream(), Charset.defaultCharset()), '$', '$');
        st = st.add("content", content);
        mockServer.expect(requestTo("https://api.openai.com/v1/chat/completions"))
            .andRespond(withSuccess(st.render(), MediaType.APPLICATION_JSON));
    }

}
