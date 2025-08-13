package com.sku.localism_be.domain.rescueReport.entity;


import com.sku.localism_be.domain.report.entity.Report;
import com.sku.localism_be.domain.voice.entity.Voice;
import com.sku.localism_be.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Arrays;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "rescue_report")
public class RescueReport extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id; // 구조 ID

  @Column(length = 1000)
  private String hospital; // 병원명

  @Column
  private Integer eta; // ETA(예상도착시간)
  //private LocalDateTime eta; // ETA(예상도착시간)

  @Column
  private Boolean isReceived; // 수신 여부

  @Column(length = 1000)
  private String recommendedResources; // 추천 자원 (comma-separated)

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "report_id", nullable = false)
  private Report report;  // 신고 고유 ID 대신 객체 참조

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "voice_id", nullable = false)
  private Voice voice;  // 음성 고유 ID 대신 객체 참조


  // ====== 리스트 변환 메서드 ======
  public List<String> sliceRecommendedResources() {
    return recommendedResources != null ? Arrays.asList(recommendedResources.split(",")) : List.of();
  }
}
