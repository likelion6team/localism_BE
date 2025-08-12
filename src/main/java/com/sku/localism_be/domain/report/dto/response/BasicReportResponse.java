package com.sku.localism_be.domain.report.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "BasicReportResponse DTO", description = "신고 리포트 응답 데이터")
public class BasicReportResponse {

  @Schema(description = "신고 리포트 ID", example = "1")
  private Long id;

}
