package com.sku.localism_be.domain.rescueReport.repository;

import com.sku.localism_be.domain.rescueReport.entity.RescueReport;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RescueReportRepository extends JpaRepository<RescueReport, Long> {
  List<RescueReport> findByIsReceivedFalseOrderByEtaAsc();
}
