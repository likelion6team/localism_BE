package com.sku.localism_be.domain.rescueReport.mapper;

import com.sku.localism_be.domain.report.entity.Report;
import com.sku.localism_be.domain.rescueReport.dto.response.DetailRescueReportResponse;
import com.sku.localism_be.domain.rescueReport.dto.response.RescueReportResponse;
import com.sku.localism_be.domain.rescueReport.entity.RescueReport;
import com.sku.localism_be.domain.vitalSign.entity.VitalSign;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RescueReportMapper {

  public DetailRescueReportResponse toDetailRescueReportResponse(RescueReport rescueReport) {
    if (rescueReport == null || rescueReport.getReport() == null) {
      return null;
    }

    Report report = rescueReport.getReport();
    VitalSign vs = report.getVitalSign();

    return DetailRescueReportResponse.builder()
        .id(rescueReport.getId())
        .reportId(report.getId())
        .created(report.getCreated())
        .location(report.getLocation())
        .eta(rescueReport.getEta())
        .details(rescueReport.getVoice().getSummary())
        .aiRecommendations(rescueReport.sliceRecommendedResources())
        .majorSymptoms(report.sliceMainSymptoms())
            .systolics(List.of(
                    vs.getSystolic10(),
                    vs.getSystolic8(),
                    vs.getSystolic6(),
                    vs.getSystolic4(),
                    vs.getSystolic2(),
                    vs.getSystolic0()
            ))
            .pulses(List.of(
                    vs.getPulse10(),
                    vs.getPulse8(),
                    vs.getPulse6(),
                    vs.getPulse4(),
                    vs.getPulse2(),
                    vs.getPulse0()
            ))
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
