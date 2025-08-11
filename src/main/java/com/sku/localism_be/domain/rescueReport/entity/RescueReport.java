package com.sku.localism_be.domain.rescueReport.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "rescue_report")
public class RescueReport {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id; // 구조 ID

  @Column(length = 1000)
  private String details; // 구체 사항

  @Column
  private LocalDateTime eta; // ETA(예상도착시간)

  @Column
  private Boolean isReceived; // 수신 여부

  @Column(nullable = false)
  private Long reportId; // 신고 고유 ID (FK)

  @Column(length = 1000)
  private String recommendedResources; // 추천 자원 (comma-separated)

  // ====== 리스트 변환 메서드 ======
  public List<String> sliceRecommendedResources() {
    return recommendedResources != null ? Arrays.asList(recommendedResources.split(",")) : List.of();
  }
}