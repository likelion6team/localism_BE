package com.sku.localism_be.domain.detailCard.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
@Schema(title="SmallReportListResponse DTO", description = "요약 리포트 리스트 응답 데이터")
public class SmallReportListResponse {

  @Schema(description = "요약 리포트 리스트")
  private List<SmallReportResponse> reportList;

}
