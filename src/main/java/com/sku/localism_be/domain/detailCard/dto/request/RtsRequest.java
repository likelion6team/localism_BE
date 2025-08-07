package com.sku.localism_be.domain.detailCard.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "RtsRequest DTO", description = "Rts 점수 결과 요청 데이터")
public class RtsRequest {

  @NotNull(message = "호흡수는 필수입니다.")
  @Min(0)
  @Schema(description = "호흡수", example = "18")
  private Integer respirationRate;

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

}
