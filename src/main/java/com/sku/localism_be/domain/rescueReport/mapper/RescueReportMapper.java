package com.sku.localism_be.domain.rescueReport.mapper;

import com.sku.localism_be.domain.report.entity.Report;
import com.sku.localism_be.domain.rescueReport.dto.response.DetailRescueReportResponse;
import com.sku.localism_be.domain.rescueReport.dto.response.RescueReportResponse;
import com.sku.localism_be.domain.rescueReport.entity.RescueReport;
import org.springframework.stereotype.Component;

@Component
public class RescueReportMapper {

  public DetailRescueReportResponse toDetailRescueReportResponse(RescueReport rescueReport) {
    if (rescueReport == null || rescueReport.getReport() == null) {
      return null;
    }

    Report report = rescueReport.getReport();

    return DetailRescueReportResponse.builder()
        .id(rescueReport.getId())
        .reportId(report.getId())
        .created(report.getCreated())
        .location(report.getLocation())
        .eta(rescueReport.getEta())
        .details(rescueReport.getDetails())
        //.keywords(null)
        .aiRecommendations(rescueReport.sliceRecommendedResources())
        .majorSymptoms(report.sliceMainSymptoms())
        .build();
  }

  public RescueReportResponse toRescueReportResponse(RescueReport rescueReport) {
    if (rescueReport == null || rescueReport.getReport() == null) {
      return null;
    }

    Report report = rescueReport.getReport();

    return RescueReportResponse.builder()
        .id(rescueReport.getId())
        .reportId(report.getId())
        .created(report.getCreated())
        .consciousness(report.getConsciousnessStatus())
        .majorSymptoms(report.sliceMainSymptoms())
        .eta(rescueReport.getEta())
        .build();
  }



}
