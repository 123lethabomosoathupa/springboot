package com.example.boardgamebuddy.chatmemory;

import org.springframework.data.repository.ListCrudRepository;

public interface ConversationRepository
         extends ListCrudRepository<Conversation, String> {
}
