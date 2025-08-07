package com.sku.localism_be.domain.detailCard.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title="RtsResponse DTO", description = "Rts 점수 결과 응답 데이터")
public class RtsResponse {

  @Schema(description = "호흡수", example = "18")
  private Integer respirationRate;

  @Schema(description = "호흡 점수(RR)", example = "1")
  private Integer respirationScore;

  @Schema(description = "최소 혈압", example = "80")
  private Integer bloodPressureMin;

  @Schema(description = "최대 혈압", example = "120")
  private Integer bloodPressureMax;

  @Schema(description = "혈압 점수(SBP)", example = "1")
  private Integer bloodPressureScore;

  @Schema(description = "의식 상태", example = "Alert")
  private String consciousness;

  @Schema(description = "의식 점수(GCS)", example = "0")
  private Integer consciousnessScore;

  @Schema(description = "총점수(RTS)", example = "3")
  private Double RTSScore;

  @Schema(description = "현상황", example = "안정")
  private String currentStatus;

  @Schema(description = "한줄 요약", example = "환자는 안정적인 상태입니다.")
  private String summary;



}
