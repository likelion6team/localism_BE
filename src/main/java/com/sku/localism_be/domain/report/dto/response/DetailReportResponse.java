package com.sku.localism_be.domain.report.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "DetailReportResponse DTO", description = "상세 신고 리포트 응답 데이터")
public class DetailReportResponse {
  @Schema(description = "신고 리포트 ID", example = "1")
  private Long id;

  @Schema(description = "사고 유형", example = "[\"교통사고\", \"화재\"]")
  private List<String> accidentType;

  @Schema(description = "주요 증상", example = "[\"가슴 통증\", \"호흡 곤란\"]")
  private List<String> majorSymptoms;

  @Schema(description = "호흡 상태", example = "정상")
  private String breathingStatus;


}
