package com.sku.localism_be.domain.report.mapper;

import com.sku.localism_be.domain.report.dto.response.DetailReportResponse;
import com.sku.localism_be.domain.report.dto.response.PostReportResponse;
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
        .id(report.getId())
        .majorSymptoms(report.sliceMainSymptoms()) // 엔티티의 문자열 → 리스트 변환 메서드 사용
        .location(report.getLocation())
        .created(report.getCreated())
        .build();
  }

  public DetailReportResponse toDetailReportResponse(Report report) {
    if (report == null) {
      return null;
    }

    return DetailReportResponse.builder()
        .id(report.getId())
        .consciousnessStatus(report.getConsciousnessStatus())
        .accidentType(report.sliceAccidentType())
        .majorSymptoms(report.sliceMainSymptoms())
        .breathingStatus(report.getBreathingStatus())
        .isPhotoPath(report.getPhotoPath() != null)
        .location(report.getLocation())
        .created(report.getCreated())
        .systolic(report.getVitalSign().getSystolic0())
        .diastolic(report.getVitalSign().getDiastolic0())
        .pulse(report.getVitalSign().getPulse0())
        .respiration(report.getVitalSign().getRespiration())
        .spo2(report.getVitalSign().getSpo2())
        .build();
  }

  public PostReportResponse toPostReportResponse(Report report) {
    if (report == null) {
      return null;
    }

    return PostReportResponse.builder()
        .id(report.getId())
        .created(report.getCreated())
        .build();
  }


}
