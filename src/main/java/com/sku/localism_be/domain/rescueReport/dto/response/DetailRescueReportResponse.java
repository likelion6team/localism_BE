package com.sku.localism_be.domain.rescueReport.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "DetailRescueReportResponse DTO", description = "상세 구조 리포트 응답 데이터")
public class DetailRescueReportResponse {

  @Schema(description = "신고 ID", example = "1")
  private Long reportId;

  @Schema(description = "위치", example = "서울시 강남구 논현로 123")
  private String location;

  @Schema(description = "상세 사항", example = "환자가 호흡 곤란을 호소하며 쓰러져 있음")
  private String details;

  @Schema(description = "키워드", example = "호흡곤란, 심정지 위험")
  private String keywords;

  @Schema(description = "AI 추천", example = "[\"심폐소생술 준비\", \"산소 공급\"]")
  private List<String> aiRecommendations;
}
