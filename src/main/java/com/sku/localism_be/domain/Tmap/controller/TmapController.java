package com.sku.localism_be.domain.Tmap.controller;

import com.sku.localism_be.domain.Tmap.dto.response.TmapNearestResponse;
import com.sku.localism_be.domain.Tmap.service.TmapService;
import com.sku.localism_be.domain.report.dto.request.ReportRequest;
import com.sku.localism_be.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tmap")
@Tag(name = "Tmap", description = "Tmap 연동 API")
public class TmapController {

  private final TmapService tmapService;

  // GET: /api/tmap/nearest?lat=..&lng=..
  @Operation(summary = "주변 최단 ETA 병원/응급실 조회 (GET, 쿼리 파라미터)")
  @GetMapping("/nearest")
  public ResponseEntity<BaseResponse<TmapNearestResponse>> nearestGet(
      @RequestParam double lat, @RequestParam double lng) {

    TmapNearestResponse res = tmapService.findNearest(lat, lng);
    return ResponseEntity.ok(BaseResponse.success("주변 최단 ETA 병원/응급실 조회 성공", res));
  }

  // POST: /api/tmap/nearest  (ReportRequest 바디)
  @Operation(summary = "주변 최단 ETA 병원/응급실 조회 (POST, ReportRequest 바디)")
  @PostMapping(value = "/nearest", consumes = "application/json")
  public ResponseEntity<BaseResponse<TmapNearestResponse>> nearestPost(
      @RequestBody @Valid ReportRequest req) {

    TmapNearestResponse res = tmapService.findNearest(req.getLat(), req.getLng());
    return ResponseEntity.ok(BaseResponse.success("주변 최단 ETA 병원/응급실 조회 성공", res));
  }
}
