package com.masingita.chatbot.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.masingita.chatbot.model.CountryInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Enhanced country data service with API integration and caching.
 * Fetches country data from external API with fallback to local data.
 */
@Service
@Slf4j
public class CountryDataService {

    private Map<String, CountryInfo> localCountryData;
    
    @Value("${chatbot.api.countries-url}")
    private String countriesApiUrl;
    
    @Value("${chatbot.api.retry-attempts:3}")
    private int retryAttempts;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Initialize local data as fallback mechanism
     */
    @PostConstruct
    public void init() {
        try {
            localCountryData = new ObjectMapper().readValue(
                new ClassPathResource("countries_data.json").getInputStream(),
                new TypeReference<Map<String, CountryInfo>>() {}
            );
            log.info("Loaded {} countries from local data", localCountryData.size());
        } catch (IOException e) {
            log.error("Failed to load local country data", e);
            localCountryData = new HashMap<>();
        }
    }

    /**
     * Get country property with API integration
     * @param country Country name
     * @param property Property name
     * @return Property value or error message
     */
    public String getCountryProperty(String country, String property) {
        String countryLower = country.toLowerCase();
        CountryInfo info = getCountryInfo(countryLower);
        
        if (info == null) return "Country not found";
        
        return switch (property.toLowerCase()) {
            case "capital" -> info.getCapital();
            case "nationalanimal" -> info.getNationalAnimal();
            case "nationalflower" -> info.getNationalFlower();
            case "population" -> info.getFormattedPopulation();
            case "area" -> info.getFormattedArea();
            case "region" -> info.getRegion();
            case "languages" -> String.join(", ", info.getLanguages());
            case "currencies" -> String.join(", ", info.getCurrencies());
            default -> "Property not available";
        };
    }

    /**
     * Get detailed country information
     * @param countryName Country name
     * @return CountryInfo object or null
     */
    @Cacheable(value = "countryDetails", key = "#countryName.toLowerCase()")
    public CountryInfo getCountryInfo(String countryName) {
        String normalizedName = countryName.toLowerCase();
        
        try {
            // Try to get from external API first
            CountryInfo apiData = fetchFromExternalApi(normalizedName);
            if (apiData != null) {
                // Enrich with local cultural data if available
                if (localCountryData.containsKey(normalizedName)) {
                    CountryInfo localData = localCountryData.get(normalizedName);
                    apiData.setNationalAnimal(localData.getNationalAnimal());
                    apiData.setNationalFlower(localData.getNationalFlower());
                    apiData.setNationalBird(localData.getNationalBird());
                }
                return apiData;
            }
        } catch (Exception e) {
            log.warn("Failed to fetch country data from API for {}: {}", countryName, e.getMessage());
        }
        
        // Fallback to local data
        return localCountryData.get(normalizedName);
    }

    /**
     * Fetch country data from external API
     * @param countryName Country name
     * @return CountryInfo from API or null
     */
    @Retryable(value = RestClientException.class, maxAttempts = 3, 
               backoff = @Backoff(delay = 1000, multiplier = 2))
    private CountryInfo fetchFromExternalApi(String countryName) {
        try {
            String url = countriesApiUrl + "/name/" + countryName + "?fullText=true";
            ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && !response.getBody().isEmpty()) {
                Map<String, Object> countryData = (Map<String, Object>) response.getBody().get(0);
                
                // Map API response to our model
                CountryInfo info = new CountryInfo();
                info.setName(countryName);
                info.enrichFromExternalData(countryData);
                
                return info;
            }
        } catch (Exception e) {
            log.warn("API request failed for country {}: {}", countryName, e.getMessage());
            throw new RestClientException("Failed to fetch from external API", e);
        }
        return null;
    }

    /**
     * List countries starting with prefix
     * @param prefix Country name prefix
     * @return Array of matching country names
     */
    @Cacheable(value = "countryMatches", key = "#prefix.toLowerCase()")
    public String[] listCountriesWhichStartsWith(String prefix) {
        String prefixLower = prefix.toLowerCase();
        
        try {
            // Try API first for broader coverage
            String url = countriesApiUrl + "/all?fields=name";
            ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<String> apiCountries = response.getBody().stream()
                    .map(country -> {
                        Map<String, Object> countryMap = (Map<String, Object>) country;
                        Map<String, String> nameMap = (Map<String, String>) countryMap.get("name");
                        return nameMap.get("common").toLowerCase();
                    })
                    .filter(name -> name.startsWith(prefixLower))
                    .collect(Collectors.toList());
                
                if (!apiCountries.isEmpty()) {
                    return apiCountries.toArray(new String[0]);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to fetch country list from API: {}", e.getMessage());
        }
        
        // Fallback to local data
        return localCountryData.keySet().stream()
            .filter(c -> c.startsWith(prefixLower))
            .toArray(String[]::new);
    }

    /**
     * List all available countries
     * @return Array of all country names
     */
    @Cacheable(value = "countries")
    public String[] listAllCountries() {
        try {
            // Try API first
            String url = countriesApiUrl + "/all?fields=name";
            ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<String> apiCountries = response.getBody().stream()
                    .map(country -> {
                        Map<String, Object> countryMap = (Map<String, Object>) country;
                        Map<String, String> nameMap = (Map<String, String>) countryMap.get("name");
                        return nameMap.get("common").toLowerCase();
                    })
                    .collect(Collectors.toList());
                
                if (!apiCountries.isEmpty()) {
                    return apiCountries.toArray(new String[0]);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to fetch all countries from API: {}", e.getMessage());
        }
        
        // Fallback to local data
        return localCountryData.keySet().toArray(new String[0]);
    }
}