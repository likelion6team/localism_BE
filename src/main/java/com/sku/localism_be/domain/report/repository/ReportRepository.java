package com.sku.localism_be.domain.report.repository;


import com.sku.localism_be.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {

}
