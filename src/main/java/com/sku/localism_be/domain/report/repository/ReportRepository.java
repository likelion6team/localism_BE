package com.sku.localism_be.domain.report.repository;


import com.sku.localism_be.domain.report.entity.Report;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
  List<Report> findByIsRescueFalseOrderByCreatedDesc();
}
