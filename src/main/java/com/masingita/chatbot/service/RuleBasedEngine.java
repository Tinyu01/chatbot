package com.masingita.chatbot.service;

import com.masingita.chatbot.model.ConversationContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Enhanced rule-based engine for chatbot logic.
 * Implements improved conversation flow and error handling.
 */
@Component
@Slf4j
public class RuleBasedEngine {
    
    @Autowired
    private ChatbotService chatbotService;
    
    private static final String WELCOME_MESSAGE = 
        "👋 Welcome to the Enhanced Country Chatbot! I can provide information about countries around the world.\n" +
        "Please enter a country name to get started.";
        
    private static final String COUNTRY_OPTIONS_MESSAGE = """
        What would you like to know about %s?
        
        A) Capital
        B) National Animal
        C) National Flower
        D) Population and Area
        E) All Information
        F) Choose another country
        G) Exit
        
        You can also toggle detailed mode by typing "detailed" or "simple".
        """;
    
    private static final Pattern OPTION_PATTERN = Pattern.compile("^[A-Ga-g]$");

    /**
     * Process user input based on conversation context
     * @param message User message
     * @param context Conversation context
     * @return Chatbot response
     */
    public String processUserInput(String message, ConversationContext context) {
        // Track conversation state
        context.incrementInteraction();
        context.setLastQuery(message);
        
        // Check for special commands
        if (message.equalsIgnoreCase("help")) {
            return getHelpMessage(context);
        } else if (message.equalsIgnoreCase("detailed")) {
            context.setDetailedMode(true);
            return "Detailed mode activated. You'll receive more comprehensive information about countries.";
        } else if (message.equalsIgnoreCase("simple")) {
            context.setDetailedMode(false);
            return "Simple mode activated. You'll receive basic information about countries.";
        }
        
        // Process based on conversation step
        String step = context.getCurrentStep();
        String response;
        
        if (context.isFirstInteraction()) {
            response = WELCOME_MESSAGE;
            context.setCurrentStep("SELECT_COUNTRY");
            return response;
        }

        try {
            if ("SELECT_COUNTRY".equals(step)) {
                response = handleCountrySelection(message, context);
            } else if ("CHOOSE_OPTION".equals(step)) {
                response = handleOptionSelection(message, context);
            } else {
                log.warn("Unexpected conversation step: {}", step);
                context.setCurrentStep("SELECT_COUNTRY");
                response = "Let's start over. Please enter a country name.";
            }
        } catch (Exception e) {
            log.error("Error processing input: {}", e.getMessage(), e);
            response = "I encountered an error. Let's try again. Please enter a country name.";
            context.setCurrentStep("SELECT_COUNTRY");
        }
        
        return response;
    }
    
    /**
     * Handle country selection phase
     * @param message User message
     * @param context Conversation context
     * @return Response message
     */
    private String handleCountrySelection(String message, ConversationContext context) {
        String[] matches = chatbotService.listCountriesWhichStartsWith(message);
        
        if (matches.length == 0) {
            return "No country found matching '" + message + "'.\n" +
                   "Please enter a valid country name.";
        } else if (matches.length > 1) {
            return "Multiple matches found: " + String.join(", ", matches) + ".\n" +
                   "Please be more specific.";
        } else {
            String country = matches[0];
            context.updateSelectedCountry(country);
            context.setCurrentStep("CHOOSE_OPTION");
            return String.format("Selected %s.\n\n%s", 
                country, 
                String.format(COUNTRY_OPTIONS_MESSAGE, country));
        }
    }
    
    /**
     * Handle option selection phase
     * @param message User message
     * @param context Conversation context
     * @return Response message
     */
    private String handleOptionSelection(String message, ConversationContext context) {
        String country = context.getSelectedCountry();
        
        if (!OPTION_PATTERN.matcher(message).matches()) {
            return "Invalid option. Please select one of the options (A-G).\n\n" + 
                   String.format(COUNTRY_OPTIONS_MESSAGE, country);
        }
        
        String optionResponse;
        String option = message.toUpperCase();
        
        switch (option) {
            case "A" -> optionResponse = "The capital of " + country + " is " + 
                        chatbotService.getCountryProperty(country, "capital") + ".";
                        
            case "B" -> optionResponse = "The national animal of " + country + " is " + 
                        chatbotService.getCountryProperty(country, "nationalAnimal") + ".";
                        
            case "C" -> optionResponse = "The national flower of " + country + " is " + 
                        chatbotService.getCountryProperty(country, "nationalFlower") + ".";
                        
            case "D" -> optionResponse = "Population: " + chatbotService.getCountryProperty(country, "population") + 
                        "\nArea: " + chatbotService.getCountryProperty(country, "area");
                        
            case "E" -> {
                return chatbotService.formatCountryInfo(country, context.isDetailedMode()) + 
                       "\n\n" + String.format(COUNTRY_OPTIONS_MESSAGE, country);
            }
            
            case "F" -> {
                context.setCurrentStep("SELECT_COUNTRY");
                return "Please enter a new country name.";
            }
            
            case "G" -> {
                context.setCurrentStep("EXIT");
                return "Thank you for using the Enhanced Country Chatbot! Goodbye!";
            }
            
            default -> {
                log.warn("Unexpected option: {}", option);
                return "An error occurred. Please try again.\n\n" + 
                       String.format(COUNTRY_OPTIONS_MESSAGE, country);
            }
        }
        
        return optionResponse + "\n\n" + String.format(COUNTRY_OPTIONS_MESSAGE, country);
    }
    
    /**
     * Get help message based on context
     * @param context Conversation context
     * @return Help message
     */
    private String getHelpMessage(ConversationContext context) {
        String step = context.getCurrentStep();
        
        if ("SELECT_COUNTRY".equals(step)) {
            return "Please enter the name of a country you'd like to learn about. " +
                   "I'll tell you about its capital, national symbols, and more!";
        } else if ("CHOOSE_OPTION".equals(step)) {
            return "Please select an option (A-G) to learn about " + context.getSelectedCountry() + 
                   ".\n\n" + String.format(COUNTRY_OPTIONS_MESSAGE, context.getSelectedCountry());
        } else {
            return "I can provide information about countries. Enter a country name to get started.";
        }
    }
}