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

  @Schema(description = "주요 증상", example = "[\"가슴 통증\", \"호흡 곤란\"]")
  private List<String> majorSymptoms;

  @Schema(description = "수축기 혈압", example = "[126, 105, 127, 130, 109, 90]")
  private List<Integer> systolics;

  @Schema(description = "심박수", example = "[68, 95, 74, 77, 76, 88]")
  private List<Integer> pulses;
  
  
}
