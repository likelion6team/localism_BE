package com.sku.localism_be.domain.voice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostEditResponse {
  private String corrected; // 교정된 문장
  private String summary;   // 두 문장 요약(응급 조치 포함)
}