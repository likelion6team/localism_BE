package com.sku.localism_be.domain.report.mapper;

import com.sku.localism_be.domain.report.dto.response.ReportResponse;
import com.sku.localism_be.domain.report.entity.Report;
import org.springframework.stereotype.Component;

@Component
public class ReportMapper {

  public ReportResponse toReportResponse(Report report) {
    if (report == null) {
      return null;
    }

    return ReportResponse.builder()
        .majorSymptoms(report.sliceMainSymptoms()) // 엔티티의 문자열 → 리스트 변환 메서드 사용
        .location(report.getLocation())
        .created(report.getCreated())
        .build();
  }
}
