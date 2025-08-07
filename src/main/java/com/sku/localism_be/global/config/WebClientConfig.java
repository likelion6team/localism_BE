package com.sku.localism_be.global.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

  @Value("${openai.api.key}")
  private String openAiApiKey;

  @Bean
  public WebClient openAiWebClient() {
    return WebClient.builder()
        .baseUrl("https://api.openai.com")
        .defaultHeader("Authorization", "Bearer " + openAiApiKey)
        .defaultHeader("Content-Type", "application/json")
        .build();
  }

}
