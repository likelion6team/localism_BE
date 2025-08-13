package com.sku.localism_be.domain.voice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public class TranscribeRequest {
  @Schema(description = "언어 힌트(선택)", example = "ko")
  private String language;
  public String getLanguage() { return language; }
  public void setLanguage(String language) { this.language = language; }
}