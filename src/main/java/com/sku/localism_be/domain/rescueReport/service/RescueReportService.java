package com.sku.localism_be.domain.rescueReport.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sku.localism_be.domain.report.entity.Report;
import com.sku.localism_be.domain.report.exception.ReportErrorCode;
import com.sku.localism_be.domain.report.repository.ReportRepository;
import com.sku.localism_be.domain.rescueReport.dto.request.RescueReportRequest;
import com.sku.localism_be.domain.rescueReport.dto.response.DetailRescueReportResponse;
import com.sku.localism_be.domain.rescueReport.dto.response.PostRescueReportResponse;
import com.sku.localism_be.domain.rescueReport.dto.response.RescueReportListResponse;
import com.sku.localism_be.domain.rescueReport.dto.response.RescueReportResponse;
import com.sku.localism_be.domain.rescueReport.entity.RescueReport;
import com.sku.localism_be.domain.rescueReport.exception.RescueReportErrorCode;
import com.sku.localism_be.domain.rescueReport.mapper.RescueReportMapper;
import com.sku.localism_be.domain.rescueReport.repository.RescueReportRepository;
import com.sku.localism_be.global.exception.CustomException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class RescueReportService {

  private final RescueReportRepository rescueReportRepository;
  private final RescueReportMapper rescueReportMapper;
  private final ReportRepository reportRepository;

  private final WebClient openAiWebClient;
  private final ObjectMapper objectMapper;

  @Value("${openai.api.key}")
  private String openAiApiKey;

  @Transactional
  public PostRescueReportResponse inputRescueReport(RescueReportRequest request) {
    // 사고 리포트가 구조 되었는지 확인
    boolean reported = rescueReportRepository.existsByReportId(request.getReportId());
    if (reported) {
      throw new CustomException(RescueReportErrorCode.RESCUE_REPORT_ALREADY_EXISTS);
    }


    // 일치하는 사고 리포트 가져오기
    Report report = reportRepository.findById(request.getReportId()).orElseThrow(() -> new CustomException(
        ReportErrorCode.REPORT_NOT_FOUND));



    // 병원 예상 시간 로직
    String hospital = "강남베드로병원";
    int time = 7;


    // 음성 인식 로직
    String rescuerDetails = request.getDetails();


    // ai 추천 조치

    // ai 프롬프트
    Map<String, Object> patientData = new LinkedHashMap<>();
    patientData.put("환자 의식 상태", report.getConsciousnessStatus());
    patientData.put("호흡 상태", report.getBreathingStatus());
    patientData.put("사고 유형", report.getAccidentType());
    patientData.put("주요 증상", report.getMainSymptoms());
    patientData.put("구조대원 현장 보고", rescuerDetails);

    String patientJson;
    try {
      patientJson = objectMapper.writeValueAsString(patientData);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("환자 데이터 JSON 변환 실패", e);
    }


    // 5. AI 프롬프트 구성
    String prompt = """
당신은 응급실 전문의입니다.
아래 환자는 현재 구급차로 병원에 이송 중이며, 이송 시간은 약 %d분입니다.
환자 데이터를 종합 분석하여, 병원 도착 직후 시행해야 할 전문적인 응급 처치 3가지를 제시하세요.

조건:
- 각 처치는 15자 이내
- '진행' 또는 '시행'으로 끝날 것
- 반드시 JSON 배열만 반환 (백틱, 코드 블록 없이)
- 예: ["기관삽관 시행", "심전도 모니터링 진행", "정맥로 확보 시행"]

환자 데이터:
%s
""".formatted(time, patientJson);

    // 6. OpenAI API 호출
    String openAiResponse = openAiWebClient.post()
        .uri("/v1/chat/completions")
        .header("Authorization", "Bearer " + openAiApiKey)
        .header("Content-Type", "application/json")
        .bodyValue(Map.of(
            "model", "gpt-4o-mini",
            "temperature", 0.4,
            "messages", List.of(
                Map.of("role", "system", "content", "너는 응급실 전문의이다."),
                Map.of("role", "user", "content", prompt)
            )
        ))
        .retrieve()
        .bodyToMono(String.class)
        .block();

    // 7. 응답 파싱
    List<String> recommendations;
    try {
      JsonNode root = objectMapper.readTree(openAiResponse);
      String content = root.path("choices").get(0).path("message").path("content").asText();

      // ```json ... ``` 제거 (만약 포함돼 있으면)
      content = content.replaceAll("(?s)^```json\\s*", "").replaceAll("(?s)```\\s*$", "").trim();

      log.info("OpenAI 응답: {}", content);
      recommendations = objectMapper.readValue(content, new TypeReference<>() {});
    } catch (Exception e) {
      throw new RuntimeException("OpenAI 응답 파싱 실패", e);
    }


    // DB에 저장
    RescueReport rescueReport = RescueReport.builder()
        .details("음성 준비 중...")
        .hospital(hospital)
        .eta(time)
        .isReceived(false)
        .recommendedResources(String.join(",", recommendations))
        .report(report)
        //.voice(voice)
        .build();


    RescueReport savedReport = rescueReportRepository.save(rescueReport);

    // 해당 신고는 구조 완료 처리. 
    savedReport.getReport().setIsRescue(true);


    return PostRescueReportResponse.builder()
        .id(savedReport.getId())
        .reportId(savedReport.getReport().getId())
        .hospital(savedReport.getHospital())
        .eta(savedReport.getEta())
        .build();

  }





  // 전체 구조 리포트 다 가져오기
  @Transactional
  public RescueReportListResponse getEveryRescueReport(){
    // 구조 리포트 다 가져옴.
    List<RescueReport> everyReports = rescueReportRepository.findAll();

    // 그걸 response로 만들고 List 안에 적재.
    List<RescueReportResponse> responseList = everyReports.stream()
        .map(rescueReportMapper::toRescueReportResponse)
        .collect(Collectors.toList());

    // 그걸 ReportListResponse의 필드에 저장하고 리턴.
    return RescueReportListResponse.builder()
        .rescueReports(responseList)
        .totalCount(responseList.size())
        .build();
  }

  // 대기 중인 구조 리포트 최신순으로 가져오기 (완료여부==false, 최신 생성 일자 순)
  @Transactional
  public RescueReportListResponse getWaitRescueReport(){
    // 구조 리포트중에 구조여부가 false 인것들을 ETA 오름차순으로 가져옴.
    List<RescueReport> waitedReports = rescueReportRepository.findByIsReceivedFalseOrderByEtaAsc();

    // 그걸 response로 만들고 List 안에 적재.
    List<RescueReportResponse> responseList = waitedReports.stream()
        .map(rescueReportMapper::toRescueReportResponse)
        .collect(Collectors.toList());

    // 그걸 ReportListResponse의 필드에 저장하고 리턴.
    return RescueReportListResponse.builder()
        .rescueReports(responseList)
        .totalCount(responseList.size())
        .build();
  }



  // 단일 구조 리포트 가져오기
  @Transactional
  public DetailRescueReportResponse getRescueReport(Long id){
    // id와 일치하는 구조 리포트 가져옴.
    RescueReport report = rescueReportRepository.findById(id).orElseThrow(() -> new CustomException(
        RescueReportErrorCode.RESCUE_REPORT_NOT_FOUND));

    return rescueReportMapper.toDetailRescueReportResponse(report);
  }




  // 사고 리포트 완료 처리하는 로직.(isReceived를 true로 해서 저장)
  @Transactional
  public void completeRescueReport(Long rescueReportId) {
    // 1. 구조 리포트 조회
    RescueReport rescueReport = rescueReportRepository.findById(rescueReportId)
        .orElseThrow(() -> new CustomException(RescueReportErrorCode.RESCUE_REPORT_NOT_FOUND));

    // 2. isReceived를 true로 변경
    rescueReport.setIsReceived(true);

    // 3. @Transactional 덕분에 JPA가 자동으로 DB 반영
  }

}
