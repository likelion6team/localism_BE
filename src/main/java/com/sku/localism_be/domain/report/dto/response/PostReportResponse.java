package com.sku.localism_be.domain.report.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "PostReportResponse DTO", description = "신고 리포트 작성 응답 데이터")
public class PostReportResponse {

  @Schema(description = "신고 리포트 ID", example = "1")
  private Long id;

  @Schema(description = "사건 발생 시각", example = "2025-08-12T13:45:00")
  private LocalDateTime created;

}
