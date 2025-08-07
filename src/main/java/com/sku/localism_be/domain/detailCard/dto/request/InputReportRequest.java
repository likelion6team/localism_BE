package com.sku.localism_be.domain.detailCard.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "InputReportRequest DTO", description = "리포트 작성에 기입한 입력 내용 요청 데이터")
public class InputReportRequest {

  @NotEmpty(message = "사고유형은 필수입니다.")
  @Schema(description = "사고 유형", example = "[\"교통사고\"]")
  private List<String> accidentType;

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

  @NotNull(message = "시는 필수입니다.")
  @Min(0) @Max(23)
  @Schema(description = "시", example = "13")
  private Integer hour;

  @NotNull(message = "분은 필수입니다.")
  @Min(0) @Max(59)
  @Schema(description = "분", example = "45")
  private Integer minute;

  @NotBlank(message = "위치 정보는 필수입니다.")
  @Schema(description = "위치 정보", example = "서울시 강남구 논현로 123")
  private String location;

  @NotBlank(message = "성별은 필수입니다.")
  @Pattern(regexp = "^(M|F|기타)$", message = "성별은 'M', 'F', '기타' 중 하나여야 합니다.")
  @Schema(description = "성별", example = "F")
  private String gender;

  @NotBlank(message = "연령대는 필수입니다.")
  @Schema(description = "연령대", example = "30대")
  private String ageGroup;

  @NotEmpty(message = "증상 선택은 필수입니다.")
  @Schema(description = "주요 증상", example = "[\"가슴 통증\", \"호흡 곤란\"]")
  private List<String> majorSymptoms;

  @NotNull(message = "호흡수는 필수입니다.")
  @Min(0)
  @Schema(description = "호흡수", example = "18")
  private Integer respirationRate;

  @NotNull(message = "맥박수는 필수입니다.")
  @Min(0)
  @Schema(description = "맥박수", example = "85")
  private Integer pulseRate;

  @NotNull(message = "최소 혈압은 필수입니다.")
  @Min(0)
  @Schema(description = "최소 혈압", example = "80")
  private Integer bloodPressureMin;

  @NotNull(message = "최대 혈압은 필수입니다.")
  @Min(0)
  @Schema(description = "최대 혈압", example = "120")
  private Integer bloodPressureMax;

  @NotBlank(message = "의식 상태는 필수입니다.")
  @Schema(description = "의식 상태", example = "Alert")
  private String consciousness;

  @Schema(description = "메모", example = "현장에서 쓰러져 있음. 반응 없음.")
  private String memo;

}
