package com.sku.localism_be.domain.rescueReport.service;


import com.sku.localism_be.domain.rescueReport.mapper.RescueReportMapper;
import com.sku.localism_be.domain.rescueReport.repository.RescueReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RescueReportService {

  private final RescueReportRepository rescueReportRepository;
  private final RescueReportMapper rescueReportMapper;




}
