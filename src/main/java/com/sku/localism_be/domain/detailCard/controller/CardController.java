package com.sku.localism_be.domain.detailCard.controller;


import com.sku.localism_be.domain.detailCard.dto.response.SmallReportListResponse;
import com.sku.localism_be.domain.detailCard.service.CardService;
import com.sku.localism_be.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Cards")
@Tag(name="Card", description="Card 관리 API")
public class CardController {

  private final CardService cardService;

  // 작성한 리포트 id 배열을 받아서, 최신 5개의 요약 리포트 리스트를 리턴.
  @Operation(summary="요약 리포트 최신순 조회 API", description ="작성한 리포트 중 최신 5개의 요약 리포트 리스트 조회를 위한 API")
  @GetMapping("/latest")
  public ResponseEntity<BaseResponse<SmallReportListResponse>> getLatest(
      @RequestParam List<Long> ids) {
    SmallReportListResponse response = cardService.getLatest(ids);
    return ResponseEntity.ok(BaseResponse.success("요약 리포트 리스트 최신순 조회 응답을 성공했습니다!", response));
  }


  // 선택한 날짜와 작성한 리포트 id 배열을 받아서, 해당 일자의 요약 리포트 리스트를 리턴.
  @Operation(summary="요약 리포트 날짜별 조회 API", description ="작성한 리포트 중 해당 일자에 작성된 요약 리포트 리스트 조회를 위한 API")
  @GetMapping("/date")
  public ResponseEntity<BaseResponse<SmallReportListResponse>> getDate(
      @RequestParam List<Long> ids,
      @RequestParam int year,
      @RequestParam int month,
      @RequestParam int day
  ) {
    SmallReportListResponse response = cardService.getDate(ids, year, month, day);
    return ResponseEntity.ok(BaseResponse.success("요약 리포트 리스트 날짜별 조회 응답을 성공했습니다!", response));
  }




}
