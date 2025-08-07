package com.sku.localism_be.domain.detailCard.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title="InputReportResponse DTO", description = "리포트 요약 결과 내용 응답 데이터")
public class InputReportResponse {

  @Schema(description = "리포트 고유 ID", example = "1")
  private Long id;

  @Schema(description = "호흡수", example = "18")
  private Integer respirationRate;

  @Schema(description = "호흡 점수", example = "1")
  private Integer respirationScore;

  @Schema(description = "맥박수", example = "85")
  private Integer pulseRate;

  @Schema(description = "맥박 점수", example = "0")
  private Integer pulseScore;

  @Schema(description = "최소 혈압", example = "80")
  private Integer bloodPressureMin;

  @Schema(description = "최대 혈압", example = "120")
  private Integer bloodPressureMax;

  @Schema(description = "혈압 점수", example = "1")
  private Integer bloodPressureScore;

  @Schema(description = "의식 상태", example = "Alert")
  private String consciousness;

  @Schema(description = "의식 점수", example = "0")
  private Integer consciousnessScore;

  @Schema(description = "총점수(RTS)", example = "3")
  private Double RTSScore;

  @Schema(description = "현상황", example = "안정")
  private String currentStatus;

  @Schema(description = "한줄 요약", example = "환자는 안정적인 상태입니다.")
  private String summary;

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

  @Schema(description = "주요 증상", example = "가슴 통증, 호흡 곤란")
  private String majorSymptoms;

  @Schema(description = "AI 추천 응급 대응 조치", example = "119에 신고 후 즉시 병원 이송")
  private String aiRecommendedAction;


}
