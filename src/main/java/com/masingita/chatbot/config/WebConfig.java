package com.masingita.chatbot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

/**
 * Web configuration for the application.
 * Configures internationalization, REST template, and API clients.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${chatbot.api.timeout-seconds:10}")
    private int apiTimeoutSeconds;
    
    @Value("${chatbot.ui.default-language:en}")
    private String defaultLanguage;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new org.springframework.http.client.HttpComponentsClientHttpRequestFactory(
                org.apache.hc.client5.http.impl.classic.HttpClients.custom()
                        .setDefaultRequestConfig(org.apache.hc.client5.http.config.RequestConfig.custom()
                                .setConnectTimeout(org.apache.hc.core5.util.Timeout.ofSeconds(apiTimeoutSeconds))
                                .setResponseTimeout(org.apache.hc.core5.util.Timeout.ofSeconds(apiTimeoutSeconds))
                                .build())
                        .build()));
        return restTemplate;
    }

    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver localeResolver = new CookieLocaleResolver("LOCALE");
        localeResolver.setDefaultLocale(Locale.forLanguageTag(defaultLanguage));
        return localeResolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}