package com.sku.localism_be.domain.detailCard.entity;


import com.sku.localism_be.global.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
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
@Table(name="DetailCard")
public class DetailCard extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;


  // 생체 신호 및 점수
  @Column(nullable = true)
  private Integer respirationRate;       // 호흡수

  @Column(nullable = true)
  private Integer respirationScore;      // 호흡 점수

  @Column(nullable = true)
  private Integer pulseRate;             // 맥박수

  @Column(nullable = true)
  private Integer pulseScore;            // 맥박 점수


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


  // 날짜 및 시간
  @Column(nullable = true)
  private Integer year;

  @Column(nullable = true)
  private Integer month;

  @Column(nullable = true)
  private Integer day;

  @Column(nullable = true)
  private Integer hour;

  @Column(nullable = true)
  private Integer minute;


  // 사용자 정보
  @Column(length = 10)
  private String gender;                 // 성별 ("M", "F", "기타")

  @Column(length = 20)
  private String ageGroup;               // 연령대 ("20대", "30대", ...)


  // 사고 유형, 증상 및 조치
  @Column(length = 1000)
  private String accidentType;          // 사고 유형 (comma-separated)
  
  @Column(length = 1000)
  private String majorSymptoms;          // 주요 증상 (comma-separated)

  @Column(length = 1000)
  private String aiRecommendedAction;    // AI 추천 응급 대응 조치



  // String을 ,로 슬라이스 하여 리스트로 만드는 메서드.
  public List<String> sliceAT(){
    List<String> responseList = Arrays.asList(this.accidentType.split(","));
    return responseList;
  }

  public List<String> sliceMS(){
    List<String> responseList = Arrays.asList(this.majorSymptoms.split(","));
    return responseList;
  }

//  @Column(nullable = false)
//  private Boolean sent = false;               // 전송 여부

}
