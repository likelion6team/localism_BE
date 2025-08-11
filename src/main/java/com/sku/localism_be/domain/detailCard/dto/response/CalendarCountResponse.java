package com.sku.localism_be.domain.detailCard.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(title="CalendarCountResponse DTO", description = "해당 월의 리포트 개수 응답 데이터")
public class CalendarCountResponse {


  @Schema(description = "년", example = "2025")
  private Integer year;

  @Schema(description = "월", example = "8")
  private Integer month;

  @Schema(description = "일", example = "4")
  private Integer day;

  @Schema(description = "당일 작성된 리포트 개수", example = "3")
  private Long count;

}
