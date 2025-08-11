package com.sku.localism_be.domain.detailCard.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title="CalendarCountListResponse DTO", description = "해당 월의 리포트 개수 리스트 응답 데이터")
public class CalendarCountListResponse {

  @Schema(description = "요약 리포트 리스트 길이")
  private int resultCount;

  @Schema(description = "요약 리포트 리스트")
  private List<CalendarCountResponse> countList;


}
