package com.sku.localism_be.domain.report.dto.response;

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
@Schema(title = "ReportListResponse DTO", description = "신고 리포트 목록 응답 데이터")
public class ReportListResponse {

  @Schema(description = "신고 리포트 목록")
  private List<ReportResponse> reports;

  @Schema(description = "리스트의 총 개수", example = "5")
  private int totalCount;
}
