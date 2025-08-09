package com.sku.localism_be.domain.detailCard.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title="SmallReportResponse DTO", description = "단일 요약 리포트 데이터 응답 데이터")
public class SmallReportResponse {

  @Schema(description = "리포트 고유 ID", example = "1")
  private Long id;

  @Schema(description = "년", example = "2025")
  private Integer year;

  @Schema(description = "월", example = "8")
  private Integer month;

  @Schema(description = "일", example = "4")
  private Integer day;

  @Schema(description = "시", example = "13")
  private Integer hour;

  @Schema(description = "분", example = "45")
  private Integer minute;

  @Schema(description = "성별", example = "F")
  private String gender;

  @Schema(description = "연령대", example = "30대")
  private String ageGroup;

  @Schema(description = "발생지", example = "서울시 강남구 논현로 123")
  private String location;

  @Schema(description = "주요 증상", example = "가슴 통증, 호흡 곤란")
  private List<String> majorSymptoms;

  @Schema(description = "총점수(RTS)", example = "3")
  private Double RTSScore;


}
