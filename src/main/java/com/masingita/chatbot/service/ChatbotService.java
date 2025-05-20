package com.masingita.chatbot.service;

import com.masingita.chatbot.model.ConversationContext;
import com.masingita.chatbot.model.ConversationHistory;
import com.masingita.chatbot.model.CountryInfo;
import com.masingita.chatbot.repository.ConversationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Enhanced chatbot service providing conversation management.
 * Integrates country data service with conversation persistence.
 */
@Service
@Slf4j
public class ChatbotService {
    
    @Autowired
    private CountryDataService countryDataService;
    
    @Autowired
    private ConversationRepository conversationRepository;

    /**
     * Get property for a specific country
     * @param country Country name
     * @param property Property name
     * @return Property value or error message
     */
    public String getCountryProperty(String country, String property) {
        return countryDataService.getCountryProperty(country, property);
    }

    /**
     * List countries starting with given prefix
     * @param prefix Country name prefix
     * @return Array of matching countries
     */
    public String[] listCountriesWhichStartsWith(String prefix) {
        return countryDataService.listCountriesWhichStartsWith(prefix);
    }

    /**
     * List all available countries
     * @return Array of country names
     */
    public String[] listAllCountries() {
        return countryDataService.listAllCountries();
    }
    
    /**
     * Get detailed country information
     * @param country Country name
     * @return Country info object or null
     */
    public CountryInfo getCountryDetails(String country) {
        return countryDataService.getCountryInfo(country);
    }
    
    /**
     * Save conversation to repository
     * @param sessionId Session identifier
     * @param userId User identifier
     * @param userMessage User message
     * @param botResponse Bot response
     * @param context Conversation context
     * @return Updated conversation history
     */
    public ConversationHistory saveConversation(String sessionId, String userId, 
                                              String userMessage, String botResponse,
                                              ConversationContext context) {
        // Try to find existing conversation
        ConversationHistory conversation = conversationRepository
            .findBySessionId(sessionId)
            .orElse(new ConversationHistory());
        
        // Update conversation data
        conversation.setSessionId(sessionId);
        conversation.setUserId(userId);
        conversation.setContext(context);
        
        // Add messages
        conversation.addMessage("user", userMessage);
        conversation.addMessage("bot", botResponse);
        
        // Save to repository
        return conversationRepository.save(conversation);
    }
    
    /**
     * Find conversation by session ID
     * @param sessionId Session identifier
     * @return Conversation history if found
     */
    public Optional<ConversationHistory> findConversation(String sessionId) {
        return conversationRepository.findBySessionId(sessionId);
    }
    
    /**
     * Format country data for display
     * @param country Country name
     * @param detailedMode Whether to show detailed information
     * @return Formatted country information
     */
    public String formatCountryInfo(String country, boolean detailedMode) {
        CountryInfo info = countryDataService.getCountryInfo(country);
        if (info == null) {
            return "Country information not available.";
        }
        
        StringBuilder builder = new StringBuilder();
        builder.append("Information about ").append(info.getName()).append(":\n\n");
        
        // Basic info
        builder.append("🏛️ Capital: ").append(info.getCapital()).append("\n");
        
        if (detailedMode) {
            // Extended info for detailed mode
            if (info.getRegion() != null) {
                builder.append("🌍 Region: ").append(info.getRegion());
                if (info.getSubregion() != null) {
                    builder.append(" (").append(info.getSubregion()).append(")");
                }
                builder.append("\n");
            }
            
            if (info.getPopulation() > 0) {
                builder.append("👥 Population: ").append(info.getFormattedPopulation()).append("\n");
            }
            
            if (info.getArea() > 0) {
                builder.append("📏 Area: ").append(info.getFormattedArea()).append("\n");
            }
            
            if (!info.getLanguages().isEmpty()) {
                builder.append("🗣️ Languages: ").append(String.join(", ", info.getLanguages())).append("\n");
            }
            
            if (!info.getCurrencies().isEmpty()) {
                builder.append("💰 Currencies: ").append(String.join(", ", info.getCurrencies())).append("\n");
            }
        }
        
        // Cultural info
        if (info.getNationalAnimal() != null && !info.getNationalAnimal().equals("Unknown")) {
            builder.append("🐾 National Animal: ").append(info.getNationalAnimal()).append("\n");
        }
        
        if (info.getNationalFlower() != null && !info.getNationalFlower().equals("Unknown")) {
            builder.append("🌸 National Flower: ").append(info.getNationalFlower()).append("\n");
        }
        
        if (detailedMode && info.getNationalBird() != null && !info.getNationalBird().equals("Unknown")) {
            builder.append("🦜 National Bird: ").append(info.getNationalBird()).append("\n");
        }
        
        return builder.toString();
    }
}