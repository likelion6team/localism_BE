package com.sku.localism_be.domain.rescueReport.mapper;

import com.sku.localism_be.domain.report.entity.Report;
import com.sku.localism_be.domain.rescueReport.dto.response.DetailRescueReportResponse;
import com.sku.localism_be.domain.rescueReport.dto.response.PostRescueReportResponse;
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
        .reportId(report.getId())
        .location(report.getLocation())
        .details(rescueReport.getDetails())
        .keywords(null)
        .aiRecommendations(rescueReport.sliceRecommendedResources())
        .build();
  }

  public RescueReportResponse toRescueReportResponse(RescueReport rescueReport) {
    if (rescueReport == null || rescueReport.getReport() == null) {
      return null;
    }

    Report report = rescueReport.getReport();

    return RescueReportResponse.builder()
        .reportId(report.getId())
        .created(report.getCreated())
        .consciousness(report.getConsciousnessStatus())
        .majorSymptoms(report.sliceMainSymptoms())
        .build();
  }



}
