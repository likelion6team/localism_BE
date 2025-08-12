package com.sku.localism_be.domain.rescueReport.controller;


import com.sku.localism_be.domain.report.service.ReportService;
import com.sku.localism_be.domain.rescueReport.service.RescueReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rescueReports")
@Tag(name="RescueReport", description="RescueReport 관리 API")
public class RescueReportController {

  private final RescueReportService rescueReportService;



}
