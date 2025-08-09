package com.sku.localism_be.domain.detailCard.mapper;


import com.sku.localism_be.domain.detailCard.dto.response.InputReportResponse;
import com.sku.localism_be.domain.detailCard.entity.DetailCard;
import org.springframework.stereotype.Component;

@Component
public class DetailCardMapper {

  public InputReportResponse toInputReportResponse(DetailCard detailCard) {
    return InputReportResponse.builder()
        .id(detailCard.getId())
        .respirationRate(detailCard.getRespirationRate())
        .respirationScore(detailCard.getRespirationScore())
        .pulseRate(detailCard.getPulseRate())
        .pulseScore(detailCard.getPulseScore())
        .bloodPressureMin(detailCard.getBloodPressureMin())
        .bloodPressureMax(detailCard.getBloodPressureMax())
        .bloodPressureScore(detailCard.getBloodPressureScore())
        .consciousness(detailCard.getConsciousness())
        .consciousnessScore(detailCard.getConsciousnessScore())
        .RTSScore(detailCard.getTotalScore())
        .currentStatus(detailCard.getCurrentStatus())
        .summary(detailCard.getSummary())
        .year(detailCard.getYear())
        .month(detailCard.getMonth())
        .day(detailCard.getDay())
        .hour(detailCard.getHour())
        .minute(detailCard.getMinute())
        .gender(detailCard.getGender())
        .ageGroup(detailCard.getAgeGroup())
        .location(detailCard.getLocation())
        .accidentType(detailCard.sliceAT())
        .majorSymptoms(detailCard.sliceMS())
        .aiRecommendedAction(detailCard.sliceAi())
        .build();
  }

}
