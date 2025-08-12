package com.sku.localism_be.domain.report.controller;


import com.sku.localism_be.domain.detailCard.service.CardService;
import com.sku.localism_be.domain.report.service.ReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Reports")
@Tag(name="Report", description="Report 관리 API")
public class ReportController {

  private final ReportService reportService;

}
