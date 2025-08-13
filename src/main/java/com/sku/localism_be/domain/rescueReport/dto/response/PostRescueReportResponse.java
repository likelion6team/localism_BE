package com.sku.localism_be.domain.rescueReport.dto.response;


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
@Schema(title = "PostRescueReportResponse DTO", description = "구조 리포트 작성 응답 데이터")
public class PostRescueReportResponse {


  @Schema(description = "구조 리포트 ID", example = "1")
  private Long id;

  @Schema(description = "신고 리포트 ID", example = "1")
  private Long reportId;

  @Schema(description = "병원명", example = "강남베드로병원")
  private String hospital;

  @Schema(description = "time(예상 소요 시간)", example = "17")
  private Integer eta;

}
