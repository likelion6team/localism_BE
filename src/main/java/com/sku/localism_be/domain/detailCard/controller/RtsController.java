package com.sku.localism_be.domain.detailCard.controller;


import com.sku.localism_be.domain.detailCard.dto.request.RtsRequest;
import com.sku.localism_be.domain.detailCard.dto.response.RtsResponse;
import com.sku.localism_be.domain.detailCard.service.RtsService;
import com.sku.localism_be.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Rts")
@Tag(name="RTS", description="RTS 관리 API")
public class RtsController {

  private final RtsService rtsService;

  @Operation(summary="Rts 결과 출력 API", description ="입력받은 생체 정보로 RTS 점수를 알려주는 API")
  @PostMapping
  public ResponseEntity<BaseResponse<RtsResponse>> getRts(
      @RequestBody @Valid RtsRequest rtsRequest) {
    RtsResponse rtsResponse = rtsService.getRts(rtsRequest);
    return ResponseEntity.ok(BaseResponse.success("RTS 점수 결과 응답을 성공했습니다!", rtsResponse));
  }
  

}
