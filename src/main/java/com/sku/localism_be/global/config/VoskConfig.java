package com.sku.localism_be.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vosk.Model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
@Slf4j
public class VoskConfig {

  @Bean(destroyMethod = "close")
  public Model voskModel(@Value("${vosk.model-path}") String path) throws IOException {
    Path root = Path.of(path);
    log.info("Loading Vosk model from {}", root.toAbsolutePath());

    if (!Files.exists(root)) {
      throw new IllegalStateException("모델 폴더가 없습니다: " + root.toAbsolutePath());
    }
    boolean ok = Files.exists(root.resolve("model.conf"))
        || Files.exists(root.resolve("conf").resolve("model.conf"));
    if (!ok) {
      throw new IllegalStateException("model.conf를 찾지 못했습니다: " + root.toAbsolutePath());
    }
    return new Model(path);
  }
}
