package com.masingita.chatbot.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Enhanced conversation history model that stores complete chat sessions.
 * Persisted in MongoDB for conversation continuity and analytics.
 */
@Data
@Document(collection = "conversations")
public class ConversationHistory {
    
    @Id
    private String id;
    
    private String userId;
    private String sessionId;
    private String userAgent;
    private String ipAddress;
    
    private List<ChatMessage> messages = new ArrayList<>();
    private ConversationContext context;
    
    @LastModifiedDate
    private LocalDateTime lastUpdated;
    private LocalDateTime createdAt = LocalDateTime.now();
    
    /**
     * Represents a single message in the conversation.
     */
    @Data
    public static class ChatMessage {
        private String role; // "user" or "bot"
        private String content;
        private LocalDateTime timestamp = LocalDateTime.now();
    }
    
    /**
     * Adds a new message to the conversation history.
     */
    public void addMessage(String role, String content) {
        ChatMessage message = new ChatMessage();
        message.setRole(role);
        message.setContent(content);
        this.messages.add(message);
    }
}