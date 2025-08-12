package com.sku.localism_be.domain.report.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "ReportRequest DTO", description = "신고 리포트 작성 요청 데이터")
public class ReportRequest {

  @NotBlank(message = "환자 의식 상태는 필수입니다.")
  @Schema(description = "환자 의식 상태", example = "Alert")
  private String consciousnessStatus;

  @NotEmpty(message = "사고 유형은 필수입니다.")
  @Schema(description = "사고 유형", example = "[\"교통사고\", \"화재\"]")
  private List<String> accidentType;

  @NotEmpty(message = "주요 증상은 필수입니다.")
  @Schema(description = "주요 증상", example = "[\"호흡 곤란\", \"출혈\"]")
  private List<String> mainSymptoms;

  @NotBlank(message = "호흡 상태는 필수입니다.")
  @Schema(description = "호흡 상태", example = "정상")
  private String breathingStatus;

  @NotBlank(message = "출혈 양상 정도는 필수입니다.")
  @Schema(description = "출혈 양상 정도", example = "심함")
  private String bleedingLevel;

  @Schema(description = "기존 병력", example = "고혈압, 당뇨")
  private String medicalHistory;

  @NotNull(message = "구조 여부는 필수입니다.")
  @Schema(description = "구조 여부", example = "false")
  private Boolean isRescue;

  @NotBlank(message = "위치 정보는 필수입니다.")
  @Schema(description = "위치", example = "서울시 강남구 논현로 123")
  private String location;

  @NotNull(message = "위도는 필수입니다.")
  @Schema(description = "위도", example = "37.5665")
  private Double lat;

  @NotNull(message = "경도는 필수입니다.")
  @Schema(description = "경도", example = "126.9780")
  private Double lng;

  @NotBlank(message = "신고자는 필수입니다.")
  @Schema(description = "신고자", example = "홍길동")
  private String reporter;
}

