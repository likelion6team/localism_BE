package com.sku.localism_be.domain.detailCard.service;


import com.sku.localism_be.domain.detailCard.dto.request.InputReportRequest;
import com.sku.localism_be.domain.detailCard.dto.response.InputReportResponse;
import com.sku.localism_be.domain.detailCard.entity.DetailCard;
import com.sku.localism_be.domain.detailCard.mapper.DetailCardMapper;
import com.sku.localism_be.domain.detailCard.repository.DetailCardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DetailCardService {

  private final DetailCardRepository detailCardRepository;
  private final DetailCardMapper detailCardMapper;


  @Transactional
  public InputReportResponse inputReport(InputReportRequest request){


    // 호흡수 -> 호흡 점수(RR)
    Integer respirationScore;
    Integer respirationRate = request.getRespirationRate();

    if (respirationRate > 29) {
      respirationScore = 3;
    } else if (respirationRate >= 10 && respirationRate <= 29) {
      respirationScore = 4;
    } else if (respirationRate >= 6 && respirationRate <= 9) {
      respirationScore = 2;
    } else if (respirationRate >= 1 && respirationRate <= 5) {
      respirationScore = 1;
    } else {
      respirationScore = 0;
    }

    // 맥박수 -> 맥박 점수
    Integer pulseScore;
    Integer pulseRate = request.getPulseRate();
    if (pulseRate == null) {
      pulseScore = 0;
    } else if (pulseRate < 40 || pulseRate > 150) {
      pulseScore = 0;
    } else {
      pulseScore = 4;
    }

    // 최대 혈압 -> 혈압 점수(수축기 혈압)(SBP)
    Integer bloodPressureScore;
    Integer bloodPressureMax = request.getBloodPressureMax();

    if (bloodPressureMax > 89) {
      bloodPressureScore = 4;
    } else if (bloodPressureMax >= 76) {
      bloodPressureScore = 3;
    } else if (bloodPressureMax >= 50) {
      bloodPressureScore = 2;
    } else if (bloodPressureMax >= 1) {
      bloodPressureScore = 1;
    } else {
      bloodPressureScore = 0;
    }


    // 의식 -> 의식 상태(), 의식 점수(GCS)
    String consciousness = null;
    Integer consciousnessScore = 0;

    String cons = request.getConsciousness();

    if ("Alert".equals(cons)) {
      consciousness = "명료함";
      consciousnessScore = 4;
    } else if ("Verbal".equals(cons)) {
      consciousness = "언어 반응 있음";
      consciousnessScore = 3;
    } else if ("Pain".equals(cons)) {
      consciousness = "통증 자극 반응 있음";
      consciousnessScore = 2;
    } else if ("Unresponsive".equals(cons)) {
      consciousness = "의식 없음";
      consciousnessScore = 1;
    } else {
      consciousness = "알 수 없음";
      consciousnessScore = 0;
    }


    // 총점수 (RTS 점수)
    double totalScore = Math.round((
        0.9368 * consciousnessScore
            + 0.7326 * bloodPressureScore
            + 0.2908 * respirationScore
    ) * 100.0) / 100.0;  // 소수점 둘째 자리까지



    // 현상황 (RTS 점수에서 나온 상태)
    String currentStatus;
    if (totalScore >= 9) {
      currentStatus = "안정";
    } else if (totalScore >= 6) {
      currentStatus = "주의";
    } else {
      currentStatus = "위험";
    }

    // [AI] 한줄 요약 (ex. 환자는 심각한 상태입니다. 즉시 병원 이송이 필요합니다.) //
    String summary;
    summary = "[AI 예정] 환자는 심각한 상태입니다. 즉시 병원 이송이 필요합니다.";


    // 사고 유형, 주요 증상 List -> String 으로 만들어서 DB에 저장.
    String accidentType = String.join(",", request.getAccidentType());
    String majorSymptoms = String.join(",", request.getMajorSymptoms());




    // [AI] ai 추천 응급 대응 조치 //
    String aiRecommendedAction;
    aiRecommendedAction = "[AI 예정] 산소 투여 진행";




    // 계산한거 + request로 받은 값을 DB에 저장.
    DetailCard detailCard = DetailCard.builder()
        .respirationRate(respirationRate)
        .respirationScore(respirationScore)
        .pulseRate(pulseRate)
        .pulseScore(pulseScore)
        .bloodPressureMin(request.getBloodPressureMin())
        .bloodPressureMax(bloodPressureMax)
        .bloodPressureScore(bloodPressureScore)
        .consciousness(consciousness)
        .consciousnessScore(consciousnessScore)
        .totalScore(totalScore)
        .currentStatus(currentStatus)
        .summary(summary)
        .accidentType(accidentType)
        .majorSymptoms(majorSymptoms)
        .aiRecommendedAction(aiRecommendedAction)
        .year(request.getYear())
        .month(request.getMonth())
        .day(request.getDay())
        .hour(request.getHour())
        .minute(request.getMinute())
        .gender(request.getGender())
        .ageGroup(request.getAgeGroup())
        .build();

    detailCardRepository.save(detailCard);

    // mapper로 InputReportResponse 형태로 바꿔서 반환.
    return detailCardMapper.toInputReportResponse(detailCard);

  }


}
