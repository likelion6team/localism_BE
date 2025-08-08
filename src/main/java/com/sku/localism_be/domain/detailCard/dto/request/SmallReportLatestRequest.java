package com.sku.localism_be.domain.detailCard.dto.request;


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
@Schema(title = "SmallReportLatestRequest DTO", description = "최신 요약 리포트 데이터 요청 데이터")
public class SmallReportLatestRequest {

  @Schema(description = "자신이 작성한 리포트 id 배열", example = "[ 1, 2, 16 ]")
  private List<Long> ids;

}
