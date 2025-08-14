package com.sku.localism_be.global.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class TmapClientConfig {

  @Bean(name = "tmapWebClient")
  public WebClient tmapWebClient(
      @Value("${tmap.base-url}") String baseUrl,
      @Value("${tmap.app-key}") String appKey
  ) {
    HttpClient http = HttpClient.create()
        .responseTimeout(Duration.ofSeconds(8))
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 4000)
        .doOnConnected(conn -> conn
            .addHandlerLast(new ReadTimeoutHandler(8))
            .addHandlerLast(new WriteTimeoutHandler(8)));

    return WebClient.builder()
        .baseUrl("https://apis.openapi.sk.com")
        .clientConnector(new ReactorClientHttpConnector(http))
        .defaultHeader("appKey", appKey)
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }
}
