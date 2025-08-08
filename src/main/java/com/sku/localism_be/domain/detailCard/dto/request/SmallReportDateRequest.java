package com.sku.localism_be.domain.detailCard.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "SmallReportDateRequest DTO", description = "날짜별 요약 리포트 데이터 요청 데이터")
public class SmallReportDateRequest {

  
  @Schema(description = "자신이 작성한 리포트 id 배열", example = "[ 1, 2, 16 ]")
  private List<Long> ids;
  
  @NotNull(message = "년도는 필수입니다.")
  @Min(value = 1900, message = "년도는 1900 이상이어야 합니다.")
  @Schema(description = "년도", example = "2025")
  private Integer year;

  @NotNull(message = "월은 필수입니다.")
  @Min(1) @Max(12)
  @Schema(description = "월", example = "8")
  private Integer month;

  @NotNull(message = "일은 필수입니다.")
  @Min(1) @Max(31)
  @Schema(description = "일", example = "4")
  private Integer day;
  
  
}
