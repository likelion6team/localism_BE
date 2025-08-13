package com.sku.localism_be.domain.report.entity;



import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "report")
public class Report {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id; // 신고 고유 ID

  @Column(length = 1000)
  private String location; // 위치

  @Column
  private Double lat; // 위도

  @Column
  private Double lng; // 경도

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private LocalDateTime created; // 제출 시간 (자동 생성)

  @Column(length = 50, nullable = false)
  private String consciousnessStatus; // 환자 의식 상태

  @Column(length = 1000, nullable = false)
  private String accidentType; // 사고 유형 (comma-separated)

  @Column(length = 1000, nullable = false)
  private String mainSymptoms; // 주요 증상 (comma-separated)

  @Column(length = 50, nullable = false)
  private String breathingStatus; // 호흡 상태

  @Column(length = 500)
  private String photoPath; // 사진 저장 경로

  @Column(nullable = false)
  private Boolean isRescue = false; // 구조 여부 (기본 false)

  // ====== 리스트 변환 메서드 ======
  public List<String> sliceAccidentType() {
    return accidentType != null ? Arrays.asList(accidentType.split(",")) : List.of();
  }

  public List<String> sliceMainSymptoms() {
    return mainSymptoms != null ? Arrays.asList(mainSymptoms.split(",")) : List.of();
  }
}

