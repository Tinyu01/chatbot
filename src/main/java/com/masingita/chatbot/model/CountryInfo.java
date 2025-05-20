package com.masingita.chatbot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Enhanced CountryInfo model with comprehensive country data fields.
 * Designed to store data from external API with fallback to static data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryInfo {
    private String name;
    private String officialName;
    private String capital;
    private String region;
    private String subregion;
    private List<String> languages = new ArrayList<>();
    private List<String> currencies = new ArrayList<>();
    private long population;
    private double area;
    private String flagUrl;
    private Map<String, String> coatOfArms;
    private List<String> borders = new ArrayList<>();
    private List<String> timezones = new ArrayList<>();
    private String continents;
    private boolean independent;
    private boolean unMember;
    
    // Cultural information (may be loaded from secondary sources)
    private String nationalAnimal;
    private String nationalFlower;
    private String nationalBird;
    private String nationalAnthem;
    
    // Additional tourism data
    private List<String> majorCities = new ArrayList<>();
    private List<String> touristAttractions = new ArrayList<>();
    private String recommendedVisitingSeasons;
    
    // Derived fields for display
    private String formattedPopulation;
    private String formattedArea;
    
    /**
     * Convert from a minimal country info object
     * @param basicInfo Basic country information
     * @return Enhanced country info
     */
    public static CountryInfo fromBasicInfo(CountryInfo basicInfo) {
        return CountryInfo.builder()
                .name(basicInfo.getName())
                .capital(basicInfo.getCapital() != null ? basicInfo.getCapital() : "Unknown")
                .nationalAnimal(basicInfo.getNationalAnimal() != null ? basicInfo.getNationalAnimal() : "Unknown")
                .nationalFlower(basicInfo.getNationalFlower() != null ? basicInfo.getNationalFlower() : "Unknown")
                .build();
    }
    
    /**
     * Merge data from external API with existing data
     * @param externalData Data from external API
     */
    public void enrichFromExternalData(Map<String, Object> externalData) {
        if (externalData == null) return;
        
        // Handle nested structures from typical country APIs
        if (externalData.containsKey("name")) {
            Map<String, Object> nameData = (Map<String, Object>) externalData.get("name");
            this.name = (String) nameData.getOrDefault("common", this.name);
            this.officialName = (String) nameData.getOrDefault("official", this.officialName);
        }
        
        if (externalData.containsKey("capital")) {
            List<String> capitals = (List<String>) externalData.get("capital");
            if (capitals != null && !capitals.isEmpty()) {
                this.capital = capitals.get(0);
            }
        }
        
        // Add more mappings for other fields as needed
        this.region = (String) externalData.getOrDefault("region", this.region);
        this.subregion = (String) externalData.getOrDefault("subregion", this.subregion);
        this.population = externalData.containsKey("population") ? 
                Long.parseLong(externalData.get("population").toString()) : this.population;
        this.area = externalData.containsKey("area") ? 
                Double.parseDouble(externalData.get("area").toString()) : this.area;
                
        // Format derived fields for display
        this.formattedPopulation = formatPopulation(this.population);
        this.formattedArea = formatArea(this.area);
    }
    
    private String formatPopulation(long population) {
        if (population < 1000) return String.valueOf(population);
        if (population < 1_000_000) return String.format("%.1fK", population / 1000.0);
        if (population < 1_000_000_000) return String.format("%.1fM", population / 1_000_000.0);
        return String.format("%.1fB", population / 1_000_000_000.0);
    }
    
    private String formatArea(double area) {
        if (area <= 0) return "Unknown";
        return String.format("%,.0f km²", area);
    }
}