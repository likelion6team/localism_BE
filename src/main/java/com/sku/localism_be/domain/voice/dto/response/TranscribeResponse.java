// src/main/java/com/sku/localism_be/domain/voice/dto/response/TranscribeResponse.java
package com.sku.localism_be.domain.voice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title="TranscribeResponse", description="음성 전사 결과")
public class TranscribeResponse {
  @Schema(example="1") private Long id;
  @Schema(example="환자 혈압 80으로 떨어짐...") private String originalText; // ✅ STT 원문
  @Schema(example="환자 혈압이 80mmHg로 떨어졌습니다...") private String text; // ✅ 교정된 문장
  @Schema(example="혈압이 급격히 저하되어 즉시 수액 및 산소 공급 필요.") private String summary; // ✅ 요약(응답 전용)
  @Schema(example="5.23") private Double durationSec;
  @Schema(example="2025-08-11T14:25:03") private LocalDateTime createdAt;
}