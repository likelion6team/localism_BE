package com.sku.localism_be.domain.detailCard.service;



import com.sku.localism_be.domain.detailCard.dto.request.RtsRequest;
import com.sku.localism_be.domain.detailCard.dto.response.RtsResponse;
import com.sku.localism_be.domain.detailCard.entity.Rts;
import com.sku.localism_be.domain.detailCard.mapper.DetailCardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RtsService {

  private final DetailCardMapper detailCardMapper;

  @Transactional
  public RtsResponse getRts(RtsRequest request){

    // 점수 환산 //

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


    // RTS 계산 및 결과 //
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


    // Response로 변환해서 return //
    Rts rts = Rts.builder()
        .respirationRate(respirationRate)
        .respirationScore(respirationScore)
        .bloodPressureMin(request.getBloodPressureMin())
        .bloodPressureMax(bloodPressureMax)
        .bloodPressureScore(bloodPressureScore)
        .consciousness(consciousness)
        .consciousnessScore(consciousnessScore)
        .totalScore(totalScore)
        .currentStatus(currentStatus)
        .summary(summary)
        .build();

    return detailCardMapper.toRtsResponse(rts);

  }

}
