package com.sku.localism_be.domain.rescueReport.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
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

  @Schema(description = "구조 리포트 ID", example = "1")
  private Long id;

  @Schema(description = "신고 ID", example = "1")
  private Long reportId;

  @Schema(description = "사건 발생 시각", example = "2025-08-12T13:45:00")
  private LocalDateTime created;

  @Schema(description = "위치", example = "서울시 강남구 논현로 123")
  private String location;

  @Schema(description = "eta(예상 소요 시간)", example = "17")
  private Integer eta;

  @Schema(description = "상세 사항", example = "환자가 호흡 곤란을 호소하며 쓰러져 있음")
  private String details;

//  @Schema(description = "키워드", example = "호흡곤란, 심정지 위험")
//  private String keywords;

  @Schema(description = "AI 추천", example = "[\"심폐소생술 준비\", \"산소 공급\"]")
  private List<String> aiRecommendations;

  @Schema(description = "주요 증상", example = "[\"가슴 통증\", \"호흡 곤란\"]")
  private List<String> majorSymptoms;
}
