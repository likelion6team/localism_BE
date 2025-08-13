package com.sku.localism_be.domain.rescueReport.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "RescueReportResponse DTO", description = "구조 리포트 목록 응답 데이터")
public class RescueReportResponse {

  @Schema(description = "구조 리포트 ID", example = "1")
  private Long id;

  @Schema(description = "신고 ID", example = "1")
  private Long reportId;

  @Schema(description = "사건 발생 시각", example = "2025-08-12T13:45:00")
  private LocalDateTime created;

  @Schema(description = "의식 상태", example = "Alert")
  private String consciousness;

  @Schema(description = "주요 증상", example = "[\"가슴 통증\", \"호흡 곤란\"]")
  private List<String> majorSymptoms;

  @Schema(description = "eta(예상 소요 시간)", example = "17")
  private Integer eta;
}
