package com.sku.localism_be.domain.Tmap.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "TmapNearestResponse", description = "반경 내 가장 가까운 응급실/병원 ETA 응답")
public class TmapNearestResponse {

  @Schema(description = "카테고리(응급실/병원/N/A)", example = "응급실")
  private String category;

  @Schema(description = "병원/응급실 이름", example = "서울대병원 응급의료센터")
  private String name;

  @Schema(description = "도착 예상 시간(분)", example = "7")
  private Integer etaMinutes;
}
