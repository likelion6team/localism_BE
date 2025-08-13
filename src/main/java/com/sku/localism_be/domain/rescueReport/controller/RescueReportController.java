package com.sku.localism_be.domain.rescueReport.controller;


import com.sku.localism_be.domain.detailCard.dto.request.InputReportRequest;
import com.sku.localism_be.domain.detailCard.dto.response.InputReportResponse;
import com.sku.localism_be.domain.report.service.ReportService;
import com.sku.localism_be.domain.rescueReport.dto.request.RescueReportRequest;
import com.sku.localism_be.domain.rescueReport.dto.response.DetailRescueReportResponse;
import com.sku.localism_be.domain.rescueReport.dto.response.PostRescueReportResponse;
import com.sku.localism_be.domain.rescueReport.service.RescueReportService;
import com.sku.localism_be.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rescueReports")
@Tag(name="RescueReport", description="RescueReport 관리 API")
public class RescueReportController {

  private final RescueReportService rescueReportService;

  @Operation(summary = "구조 리포트 작성 API", description = "구급대원이 작성하는 구조 리포트 작성 API")
  @PostMapping
  public ResponseEntity<BaseResponse<PostRescueReportResponse>> inputRescueReport(
      @RequestBody @Valid RescueReportRequest rescueReportRequest) {
    PostRescueReportResponse inputReportResponse = rescueReportService.inputRescueReport(rescueReportRequest);
    return ResponseEntity.ok(BaseResponse.success("구조 리포트 작성을 성공했습니다!", inputReportResponse));
  }



}
