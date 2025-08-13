package com.sku.localism_be.domain.rescueReport.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "RescueReportRequest DTO", description = "구조 리포트 작성 요청 데이터")
public class RescueReportRequest {

  @NotNull(message = "신고 ID는 필수입니다.")
  @Schema(description = "신고 ID", example = "1")
  private Long reportId;

  @NotBlank(message = "상세 사항은 필수입니다.")
  @Schema(description = "상세 사항", example = "환자 도착 시 의식 없음, 심폐소생술 실시")
  private String details;
//
//  @NotBlank(message = "키워드는 필수입니다.")
//  @Schema(description = "키워드", example = "심폐소생술, 응급이송")
//  private String keyword;
}

