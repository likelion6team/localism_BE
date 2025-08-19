package com.sku.localism_be.domain.rescueReport.controller;


import com.sku.localism_be.domain.rescueReport.dto.request.RescueReportRequest;
import com.sku.localism_be.domain.rescueReport.dto.response.DetailRescueReportResponse;
import com.sku.localism_be.domain.rescueReport.dto.response.PostRescueReportResponse;
import com.sku.localism_be.domain.rescueReport.dto.response.RescueReportListResponse;
import com.sku.localism_be.domain.rescueReport.service.RescueReportService;
import com.sku.localism_be.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

  @Operation(summary = "지도 테스트 API", description = "구급대원이 작성하는 구조 리포트 작성 API")
  @PostMapping("/mapTest")
  public void mapTest(Long id) {
    rescueReportService.mapTest(id);
  }

  @Operation(summary = "구조 리포트 작성 API", description = "구급대원이 작성하는 구조 리포트 작성 API")
  @PostMapping
  public ResponseEntity<BaseResponse<PostRescueReportResponse>> inputRescueReport(
      @RequestBody @Valid RescueReportRequest rescueReportRequest) {
    PostRescueReportResponse inputReportResponse = rescueReportService.inputRescueReport(rescueReportRequest);
    return ResponseEntity.ok(BaseResponse.success("구조 리포트 작성을 성공했습니다!", inputReportResponse));
  }

  // (확인용) 작성된 전체 구조 리포트 리스트 조회
  @Operation(summary="(확인용) 전체 구조 리포트 리스트 조회 API", description ="작성된 전체 구조 리포트 리스트 조회를 위한 API")
  @GetMapping
  public ResponseEntity<BaseResponse<RescueReportListResponse>> getEveryRescueReport() {
    RescueReportListResponse response = rescueReportService.getEveryRescueReport();
    return ResponseEntity.ok(BaseResponse.success("전체 구조 리포트 리스트 조회 응답을 성공했습니다!", response));
  }

  // 최신 ETA순으로 완료 대기 중인 구조 리포트 리스트 조회
  @Operation(summary="대기 중인 구조 리포트 리스트 최신 ETA순 조회 API", description ="최신 ETA순으로 완료 대기 중인 구조 리포트 리스트 조회를 위한 API")
  @GetMapping("/wait")
  public ResponseEntity<BaseResponse<RescueReportListResponse>> getWaitRescueReport() {
    RescueReportListResponse response = rescueReportService.getWaitRescueReport();
    return ResponseEntity.ok(BaseResponse.success("구조 대기 중인 구조 리포트 리스트 최신 ETA순 조회 응답을 성공했습니다!", response));
  }


  // 상세 사고 리포트 조회
  @Operation(summary="상세 구조 리포트 조회 API", description ="id와 일치하는 단일 구조 리포트 조회를 위한 API")
  @GetMapping("/{id}")
  public ResponseEntity<BaseResponse<DetailRescueReportResponse>> getRescueReportById(@PathVariable Long id) {
    DetailRescueReportResponse response = rescueReportService.getRescueReport(id);
    return ResponseEntity.ok(BaseResponse.success("상세 구조 리포트 조회 응답을 성공했습니다!", response));
  }


  // 사고 리포트 구조 완료로 업데이트
  @Operation(summary="구조 리포트 완료 처리 API", description="버튼 클릭 시 구조 리포트를 완료 처리(isReceived = true) 합니다.")
  @PatchMapping("/{id}/complete")  // PATCH로 변경
  public ResponseEntity<BaseResponse<String>> completeRescueReport(@PathVariable Long id) {
    rescueReportService.completeRescueReport(id);
    return ResponseEntity.ok(BaseResponse.success("구조 리포트 완료 처리를 성공했습니다!"));
  }


}
