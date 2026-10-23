package com.example.boardgamebuddy.chatmemory;

import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.List;

public class MongoChatMemoryRepository implements ChatMemoryRepository {

  private final ConversationRepository conversationRepository;

  public MongoChatMemoryRepository(ConversationRepository conversationRepository) {
    this.conversationRepository = conversationRepository;
  }

  @Override
  public List<String> findConversationIds() {
    return conversationRepository.findAll().stream()
        .map(Conversation::conversationId)
        .toList();
  }

  @Override
  public List<Message> findByConversationId(String conversationId) {
    var conversation = conversationRepository.findById(conversationId);

    return conversation.isPresent()
        ? conversationRepository.findById(conversationId)
            .get().messages()
            .stream()
            .map(conversationMessage -> {
                Message message =
                  conversationMessage.messageType().equals(MessageType.USER.getValue()) ?
                      new UserMessage(conversationMessage.content()) :
                      new AssistantMessage(conversationMessage.content());
                return message;
            }).toList()
        : List.of();
  }

  @Override
  public void saveAll(String conversationId, List<Message> messages) {
    var conversationMessages = messages.stream()
            .map(message -> {
              return new ConversationMessage(
                  message.getText(),
                  message.getMessageType().getValue());
            })
            .toList();
    conversationRepository.save(new Conversation(conversationId, conversationMessages));
  }

  @Override
  public void deleteByConversationId(String conversationId) {
    conversationRepository.deleteById(conversationId);
  }

}
