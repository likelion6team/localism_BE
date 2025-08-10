package com.sku.localism_be.domain.detailCard.controller;


import com.sku.localism_be.domain.detailCard.dto.request.InputReportRequest;
import com.sku.localism_be.domain.detailCard.dto.response.InputReportResponse;
import com.sku.localism_be.domain.detailCard.service.DetailCardService;
import com.sku.localism_be.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/DetailCards")
@Tag(name="DetailCard", description="DetailCard 관리 API")
public class DetailCardController {

  private final DetailCardService detailCardService;


  // 리포트를 작성 받은 request를 이용해서 계산, DB에 저장하고, response로 리턴.
  @Operation(summary="리포트 작성 API", description ="리포트를 작성해 요약 결과 리포트를 만들기 위한 API")
  @PostMapping
  public ResponseEntity<BaseResponse<InputReportResponse>> inputReport(
      @RequestBody @Valid InputReportRequest inputReportRequest) {
    InputReportResponse inputReportResponse = detailCardService.inputReport(inputReportRequest);
    return ResponseEntity.ok(BaseResponse.success("상세 리포트 결과 응답을 성공했습니다!", inputReportResponse));
  }

  // id 받아서 해당 상세 리포트 데이터 리턴
  @Operation(summary="단일 상세 리포트 조회 API", description ="id를 받아서 해당 리포트의 상세 데이터를 보기 위한 API")
  @GetMapping("/{id}")
  public ResponseEntity<BaseResponse<InputReportResponse>> getDetailReport(@PathVariable Long id) {
    InputReportResponse response = detailCardService.getDetailReport(id);
    return ResponseEntity.ok(BaseResponse.success("단일 상세 리포트 조회 응답을 성공했습니다!", response));
  }

  @Operation(summary="(확인용) 전체 상세 리포트 조회 API", description ="존재하는 전체 리포트의 상세 데이터를 보기 위한 API")
  @GetMapping
  public ResponseEntity<BaseResponse<List<InputReportResponse>>> getDetailReports() {
    List<InputReportResponse> responseList = detailCardService.getDetailReports();
    return ResponseEntity.ok(BaseResponse.success("전체 상세 리포트 조회 응답을 성공했습니다!", responseList));
  }



}
