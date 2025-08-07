package com.sku.localism_be.domain.detailCard.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sku.localism_be.domain.detailCard.dto.request.InputReportRequest;
import com.sku.localism_be.domain.detailCard.dto.response.InputReportResponse;
import com.sku.localism_be.domain.detailCard.entity.DetailCard;
import com.sku.localism_be.domain.detailCard.mapper.DetailCardMapper;
import com.sku.localism_be.domain.detailCard.repository.DetailCardRepository;
import java.net.http.HttpHeaders;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class DetailCardService {

  private final DetailCardRepository detailCardRepository;
  private final DetailCardMapper detailCardMapper;

  private final WebClient openAiWebClient;
  private final ObjectMapper objectMapper;

  @Value("${openai.api.key}")
  private String openAiApiKey;


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


    // [AI] ai 추천 응급 대응 조치 //

    String requestJson;
    try {
      requestJson = objectMapper.writeValueAsString(request);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Request JSON 변환 실패", e);
    }

    String prompt = """
당신은 응급구조 전문가입니다.

아래 환자 데이터는 이미 병원으로 이송 중인 응급상황입니다.
그러므로 "병원으로 이송하세요", "즉시 병원 이송" 등은 절대 포함하지 마세요.

지금 이송 중 구급차 안에서 응급대원이 할 수 있는 응급처치 3가지를 15자 이내로 제시하세요.
각 항목은 '진행' 또는 '시행'으로 끝나야 하며, 숫자나 줄바꿈 없이, 오직 JSON 배열만으로 응답하세요.

환자 데이터:
%s
""".formatted(requestJson);

    String openAiResponse = openAiWebClient.post()
        .uri("/v1/chat/completions")
        .header("Authorization", "Bearer " + openAiApiKey)
        .header("Content-Type", "application/json")
        .bodyValue(Map.of(
            "model", "gpt-4",
            "temperature", 0.4,
            "messages", List.of(
                Map.of("role", "system", "content", "너는 응급처치 전문가야."),
                Map.of("role", "user", "content", prompt)
            )
        ))
        .retrieve()
        .bodyToMono(String.class)
        .block();


    List<String> actions;
    try {
      JsonNode root = objectMapper.readTree(openAiResponse);
      String content = root.path("choices").get(0).path("message").path("content").asText();
      log.info("✅ OpenAI 응답 본문: {}", content);
      actions = objectMapper.readValue(content, new TypeReference<>() {});
    } catch (Exception e) {
      throw new RuntimeException("OpenAI 응답 파싱 실패", e);
    }




    // 사고 유형, 주요 증상 List -> String 으로 만들어서 DB에 저장.
    String accidentType = String.join(",", request.getAccidentType());
    String majorSymptoms = String.join(",", request.getMajorSymptoms());
    String aiAnswer = String.join(",", actions);



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
        .aiRecommendedAction(aiAnswer)
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
