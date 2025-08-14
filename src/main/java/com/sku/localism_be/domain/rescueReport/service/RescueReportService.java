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
import com.sku.localism_be.domain.voice.entity.Voice;
import com.sku.localism_be.domain.voice.exception.VoiceErrorCode;
import com.sku.localism_be.domain.voice.repository.VoiceRecordRepository;
import com.sku.localism_be.global.exception.CustomException;
import java.io.IOException;
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

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;


@Service
@Slf4j
@RequiredArgsConstructor
public class RescueReportService {

  private final RescueReportRepository rescueReportRepository;
  private final RescueReportMapper rescueReportMapper;
  private final ReportRepository reportRepository;
  private final VoiceRecordRepository voiceRecordRepository;

  private final WebClient openAiWebClient;
  private final ObjectMapper objectMapper;

  @Value("${openai.api.key}")
  private String openAiApiKey;

  @Value("${tmap.app-key}")
  private String appKey;


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
    int time = 17;


    double lat = 37.56656541;  // 출발지 위도
    double lon = 126.98452047; // 출발지 경도
    String category = URLEncoder.encode("병원", StandardCharsets.UTF_8);

    String url = String.format(
        "https://apis.openapi.sk.com/tmap/pois/search/around?version=1&centerLon=%f&centerLat=%f&categories=%s&page=1&count=1&radius=2",
        lon, lat, category
    );

    HttpClient client = HttpClient.newHttpClient();
    HttpRequest requestP = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .header("Accept", "application/json")
        .header("appKey", appKey)
        .build();


    HttpResponse<String> response;

    try {
      response = client.send(requestP, HttpResponse.BodyHandlers.ofString());
      System.out.println("Status Code: " + response.statusCode());
      System.out.println("Response Body: " + response.body());


      // JSON 파싱
      ObjectMapper mapper = new ObjectMapper();
      JsonNode rootNode = mapper.readTree(response.body());

      // 병원 이름 추출
      JsonNode poisNode = rootNode.path("searchPoiInfo")
          .path("pois")
          .path("poi");
      if (poisNode.isArray() && poisNode.size() > 0) {
        String rawName = poisNode.get(0).path("name").asText();

        // 불필요한 단어 제거
        String[] removeSuffix = {"주차장", "정문", "후문", "후문입구", "건물"};
        for (String suffix : removeSuffix) {
          if (rawName.endsWith(suffix)) {
            rawName = rawName.substring(0, rawName.length() - suffix.length());
            break;
          }
        }

        // 모든 공백 제거
        rawName = rawName.replaceAll("\\s+", "");

        hospital = rawName;  // 공백 제거된 깔끔한 병원 이름 저장
        System.out.println("Hospital Name: " + hospital);
      } else {
        hospital = null;
        System.out.println("주변 병원이 없습니다.");
      }
    } catch (IOException e) {
      // 네트워크 문제
      log.error("TMAP API 호출 중 네트워크 오류 발생", e);
      throw new CustomException(RescueReportErrorCode.RESCUE_REPORT_API_ERROR);
    } catch (InterruptedException e) {
      // 스레드 인터럽트 문제
      log.error("TMAP API 호출이 중단됨", e);
      Thread.currentThread().interrupt(); // 인터럽트 상태 복원
      throw new CustomException(RescueReportErrorCode.RESCUE_REPORT_API_ERROR);
    }





    // 자동차 경로로 몇 분 걸리는지 경로 찾기 티맵 api 호출
    // 이게 response 출력한거:
    // Response Body: { "searchPoiInfo" : {  "totalCount" : 1055,  "count" : 1,  "page" : 1,  "pois" : {   "poi" : [    {     "id" : "8974399",         "name" : "을지로정형외과의원 주차장",     "telNo" : "0269255375",     "frontLat" : "37.56670428",     "frontLon" : "126.98432604",     "noorLat" : "37.56670428",     "noorLon" : "126.98432604",          "upperAddrName" : "서울",          "middleAddrName" : "중구",          "lowerAddrName" : "을지로2가",          "detailAddrName" : "",     "mlClass" : "1",     "firstNo" : "9",     "secondNo" : "10",          "roadName" : "을지로",     "buildingNo1" : "55",     "buildingNo2" : "",     "rpFlag" : "16",     "parkFlag" : "0",     "merchantFlag" : "",     "radius" : "0.05",     "dataKind" : "",     "stId" : "",     "highHhSale" : "0",     "minOilYn" : "N",     "oilBaseSdt" : "",     "hhPrice" : "0",     "ggPrice" : "0",     "llPrice" : "0",     "highHhPrice" : "0",     "highGgPrice" : "0",     "pkey" : "897439902","evChargers":{"evCharger":[]}    }   ]  } }}


    // 여기서 경로 찾기 api !!!!!
    try {
      // 출발지
      double startLat = lat;   // 예: 37.56656541
      double startLon = lon;   // 예: 126.98452047

      // 도착지 (POI response에서 frontLat, frontLon 파싱)
      JsonNode poiNode = objectMapper.readTree(response.body())
          .path("searchPoiInfo")
          .path("pois")
          .path("poi")
          .get(0);

      double endLat = poiNode.path("frontLat").asDouble();
      double endLon = poiNode.path("frontLon").asDouble();

      // TMap 경로 API POST 요청 JSON
      String routeRequestBody = objectMapper.writeValueAsString(Map.of(
          "startX", startLon,   // 경도
          "startY", startLat,   // 위도
          "endX", endLon,
          "endY", endLat,
          "reqCoordType", "WGS84GEO",   // 좌표 타입
          "resCoordType", "WGS84GEO",
          "searchOption", "0",          // 최적 경로
          "trafficInfo", "Y"            // 실시간 교통정보
      ));

      HttpRequest routeRequest = HttpRequest.newBuilder()
          .uri(URI.create("https://apis.openapi.sk.com/tmap/routes?version=1"))
          .header("Accept", "application/json")
          .header("Content-Type", "application/json")
          .header("appKey", appKey)
          .POST(HttpRequest.BodyPublishers.ofString(routeRequestBody))
          .build();

      HttpResponse<String> routeResponse = client.send(routeRequest, HttpResponse.BodyHandlers.ofString());

      // 응답 JSON 파싱해서 예상 소요 시간 추출
      JsonNode routeRoot = objectMapper.readTree(routeResponse.body());
      JsonNode features = routeRoot.path("features");
      int etaMinutes = 0;

      if (features.isArray() && features.size() > 0) {
        // properties -> totalTime (단위: 분)
        etaMinutes = features.get(0).path("properties").path("totalTime").asInt();
      }

      log.info("출발지 -> 도착지 예상 소요 시간(초): {}", etaMinutes);
      time = etaMinutes;  // AI 프롬프트에 반영
    } catch (IOException | InterruptedException e) {
      log.error("TMAP 경로 API 호출 오류", e);
      throw new CustomException(RescueReportErrorCode.RESCUE_REPORT_API_ERROR);
    }








    // 음성 인식 로직
    Voice voice = voiceRecordRepository.findById(request.getVoiceId()).orElseThrow(() -> new CustomException(
        VoiceErrorCode.VOICE_NOT_FOUND));
    String rescuerDetails = voice.getText();


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
        .hospital(hospital)
        .eta(time)
        .isReceived(false)
        .recommendedResources(String.join(",", recommendations))
        .report(report)
        .voice(voice)
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
