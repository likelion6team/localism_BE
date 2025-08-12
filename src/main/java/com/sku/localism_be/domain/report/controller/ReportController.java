package com.sku.localism_be.domain.report.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sku.localism_be.domain.detailCard.dto.response.SmallReportListResponse;
import com.sku.localism_be.domain.report.dto.request.ReportRequest;
import com.sku.localism_be.domain.report.dto.response.BasicReportResponse;
import com.sku.localism_be.domain.report.dto.response.ReportListResponse;
import com.sku.localism_be.domain.report.service.ReportService;
import com.sku.localism_be.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
@Tag(name="Report", description="Report 관리 API")
public class ReportController {

  private final ReportService reportService;

/*
  // (실제 배포 연동용 ) 리포트를 작성 받은 request를 이용해서 계산, DB에 저장하고, response로 리턴.
  @Operation(summary = "신고 리포트 작성 API", description = "신고자가 작성하는 신고 리포트 작성 API")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<BasicReportResponse>> inputReport(
      @Parameter(description = "신고 리포트 데이터", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
      @RequestPart("data") @Valid ReportRequest request,
      @RequestPart(value = "image", required = false) MultipartFile image) {
    BasicReportResponse response = reportService.inputReport(request, image);
    return ResponseEntity.ok(BaseResponse.success("상세 리포트 결과 응답을 성공했습니다!", response));
  }
  */

  // API 테스트용 (+ Json 파싱)
  @PostMapping(value = "/test-input-report", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<BasicReportResponse>> testInputReport(
      @RequestParam("data") String dataJson,
      @RequestPart(value = "image", required = false) MultipartFile image) throws JsonProcessingException {

    ObjectMapper mapper = new ObjectMapper();
    ReportRequest request = mapper.readValue(dataJson, ReportRequest.class);

    BasicReportResponse response = reportService.inputReport(request, image);
    return ResponseEntity.ok(BaseResponse.success("상세 리포트 결과 응답을 성공했습니다!", response));
  }

  // (확인용) 작성된 전체 신고 리포트 리스트 조회
  @Operation(summary="(확인용) 전체 신고 리포트 리스트 조회 API", description ="작성된 전체 신고 리포트 리스트 조회를 위한 API")
  @GetMapping
  public ResponseEntity<BaseResponse<ReportListResponse>> getEveryReport() {
    ReportListResponse response = reportService.getEveryReport();
    return ResponseEntity.ok(BaseResponse.success("전체 신고 리포트 리스트 조회 응답을 성공했습니다!", response));
  }

  // 최신순으로 구조 대기 중인 리포트 리스트 조회
  @Operation(summary="대기 중인 신고 리포트 리스트 최신순 조회 API", description ="최신순으로 구조 대기 중인 신고 리포트 리스트 조회를 위한 API")
  @GetMapping("/wait")
  public ResponseEntity<BaseResponse<ReportListResponse>> getWaitReport() {
    ReportListResponse response = reportService.getWaitReport();
    return ResponseEntity.ok(BaseResponse.success("구조 대기 중인 신고 리포트 리스트 최신순 조회 응답을 성공했습니다!", response));
  }

}
