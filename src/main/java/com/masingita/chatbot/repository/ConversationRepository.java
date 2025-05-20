package com.masingita.chatbot.repository;

import com.masingita.chatbot.model.ConversationHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * MongoDB repository for conversation persistence.
 * Provides methods for storing and retrieving conversation history.
 */
@Repository
public interface ConversationRepository extends MongoRepository<ConversationHistory, String> {
    
    /**
     * Find most recent conversation by user ID
     * @param userId User identifier
     * @return Latest conversation or empty
     */
    Optional<ConversationHistory> findTopByUserIdOrderByLastUpdatedDesc(String userId);
    
    /**
     * Find conversation by session ID
     * @param sessionId Session identifier
     * @return Matching conversation or empty
     */
    Optional<ConversationHistory> findBySessionId(String sessionId);
    
    /**
     * Find all conversations for a user
     * @param userId User identifier
     * @return List of conversations
     */
    List<ConversationHistory> findByUserIdOrderByLastUpdatedDesc(String userId);
    
    /**
     * Find conversations within a date range
     * @param start Start date
     * @param end End date
     * @return List of conversations
     */
    List<ConversationHistory> findByLastUpdatedBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * Find conversations with specific country context
     * @param country Country name
     * @return List of conversations
     */
    @Query("{'context.selectedCountry': ?0}")
    List<ConversationHistory> findBySelectedCountry(String country);
    
    /**
     * Delete conversations older than specified date
     * @param date Cutoff date
     * @return Count of deleted records
     */
    long deleteByLastUpdatedBefore(LocalDateTime date);
}