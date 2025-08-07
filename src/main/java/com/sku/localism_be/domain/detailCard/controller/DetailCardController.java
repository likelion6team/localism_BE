package com.sku.localism_be.domain.detailCard.controller;


import com.sku.localism_be.domain.detailCard.dto.request.InputReportRequest;
import com.sku.localism_be.domain.detailCard.dto.response.InputReportResponse;
import com.sku.localism_be.domain.detailCard.service.DetailCardService;
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


}
