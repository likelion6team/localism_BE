package com.sku.localism_be.domain.voice.mapper;

import com.sku.localism_be.domain.voice.dto.response.TranscribeResponse;
import com.sku.localism_be.domain.voice.entity.Voice;

public class VoiceMapper {
  public static TranscribeResponse toResponse(Voice v) {
    return TranscribeResponse.builder()
        .id(v.getId())
        .text(v.getText())
        .durationSec(v.getDurationSec())
        .createdAt(v.getCreatedAt())
        .build();
  }
}