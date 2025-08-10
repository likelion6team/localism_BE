package com.sku.localism_be.domain.detailCard.entity;

import com.sku.localism_be.global.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name="Rts")
public class Rts extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;


  // 생체 신호 및 점수
  @Column(nullable = true)
  private Integer respirationRate;       // 호흡수

  @Column(nullable = true)
  private Integer respirationScore;      // 호흡 점수

  // 혈압 (예: "120/80")
  @Column(length = 20)
  private Integer bloodPressureMin;     // 80

  @Column(length = 20)
  private Integer bloodPressureMax;     // 120

  @Column(nullable = true)
  private Integer bloodPressureScore;    // 혈압 점수

  @Column(length = 20)
  private String consciousness;          // 의식 상태 (예: "Alert", "Verbal", "Pain", "Unresponsive")

  @Column(nullable = true)
  private Integer consciousnessScore;    // 의식 점수

  @Column(nullable = true)
  private Double totalScore;            // 총점수

  @Column(length = 100)
  private String currentStatus;          // 현상황

  @Column(length = 255)
  private String summary;                // 한줄 요약

}
