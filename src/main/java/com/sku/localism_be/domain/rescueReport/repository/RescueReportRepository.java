package com.sku.localism_be.domain.rescueReport.repository;

import com.sku.localism_be.domain.rescueReport.entity.RescueReport;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RescueReportRepository extends JpaRepository<RescueReport, Long> {

}
