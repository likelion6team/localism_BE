package com.sku.localism_be.domain.report.service;


import com.sku.localism_be.domain.report.mapper.ReportMapper;
import com.sku.localism_be.domain.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {

  private final ReportRepository reportRepository;
  private final ReportMapper reportMapper;




}
