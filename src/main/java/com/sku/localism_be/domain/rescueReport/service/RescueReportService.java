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
import java.util.ArrayList;
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
    log.info("[RescueReport] reportID:{} 구조 리포트 작성 시작.", request.getReportId());
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


    double lat = report.getLat();  // 출발지 위도
    double lon = report.getLng(); // 출발지 경도
    String category = URLEncoder.encode("응급실", StandardCharsets.UTF_8);

    // 근처 응급실 200개 뽑기
    String url = String.format(
            "https://apis.openapi.sk.com/tmap/pois/search/around?version=1&centerLon=%f&centerLat=%f&categories=%s&page=1&count=200&radius=0&multiPoint=Y&sort=distance",
            lon, lat, category
    );

    HttpClient client = HttpClient.newHttpClient();
    HttpRequest requestP = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .header("Accept", "application/json")
        .header("appKey", appKey)
        .build();


    HttpResponse<String> response;
    // 응급실 병원 리스트
    List<JsonNode> erHospitals = new ArrayList<>();
    String rawName = "";

    // 장소, 병원명
    JsonNode poi = null;
    String nnn = null;

    try {
      response = client.send(requestP, HttpResponse.BodyHandlers.ofString());


      // JSON 파싱
      ObjectMapper mapper = new ObjectMapper();
      JsonNode rootNode = mapper.readTree(response.body());

      // 병원 이름 추출
      JsonNode poisNode = rootNode.path("searchPoiInfo")
          .path("pois")
          .path("poi");
      if (poisNode.isArray() && poisNode.size() > 0) {
        for (int i = 0; i < poisNode.size(); i++) {
          poi = poisNode.get(i);
          nnn = poi.path("name").asText();
          // 모든 공백 제거
          nnn = nnn.replaceAll("\\s+", "");

          // "응급" 포함된 병원만 필터링
          if (nnn.contains("응급")) {
            erHospitals.add(poi);
          }

        }

        if (!erHospitals.isEmpty()) {
          JsonNode nearestER = erHospitals.get(0);
          rawName = nearestER.path("name").asText();
          double endLat = nearestER.path("frontLat").asDouble();
          double endLon = nearestER.path("frontLon").asDouble();

          System.out.println("가장 가까운 응급실: " + rawName + " (" + endLat + ", " + endLon + ")");

          // 👉 이 endLat, endLon을 경로 API에 넣으면 됨
        } else {
          System.out.println("근처에 응급실이 없습니다.");
        }
        hospital = rawName;
      } else {
        System.out.println("검색된 병원이 없습니다.");
        hospital = null;
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


    // DB에 저장
    RescueReport rescueReport = RescueReport.builder()
        .hospital(hospital)
        .eta(time)
        .isReceived(false)
        .report(report)
        .voice(voice)
        .build();


    RescueReport savedReport = rescueReportRepository.save(rescueReport);

    // 해당 신고는 구조 완료 처리. 
    savedReport.getReport().setIsRescue(true);
    log.info("[RescueReport] reportID:{} 구조 리포트 작성 완료. ID:{}", request.getReportId(), savedReport.getId());

    return PostRescueReportResponse.builder()
        .id(savedReport.getId())
        .reportId(savedReport.getReport().getId())
        .hospital(savedReport.getHospital())
        .eta(savedReport.getEta())
        .build();

  }

  // 지도 테스트
  @Transactional
  public void mapTest(Long id){
    // 사고 리포트가 구조 되었는지 확인
    boolean reported = rescueReportRepository.existsByReportId(id);
    if (reported) {
      throw new CustomException(RescueReportErrorCode.RESCUE_REPORT_ALREADY_EXISTS);
    }


    // 일치하는 사고 리포트 가져오기
    Report report = reportRepository.findById(id).orElseThrow(() -> new CustomException(
            ReportErrorCode.REPORT_NOT_FOUND));



    // 병원 예상 시간 로직
    String hospital = "강남베드로병원";
    int time = 17;


    double lat = report.getLat();  // 출발지 위도
    double lon = report.getLng(); // 출발지 경도
    String category = URLEncoder.encode("응급실", StandardCharsets.UTF_8);

    // 근처 병원 200개 뽑기
    String url = String.format(
            "https://apis.openapi.sk.com/tmap/pois/search/around?version=1&centerLon=%f&centerLat=%f&categories=%s&page=1&count=200&radius=0&multiPoint=Y&sort=distance",
            lon, lat, category
    );

    HttpClient client = HttpClient.newHttpClient();
    HttpRequest requestP = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Accept", "application/json")
            .header("appKey", appKey)
            .build();


    HttpResponse<String> response;
    // 응급실 병원 리스트
    List<JsonNode> erHospitals = new ArrayList<>();
    String rawName = "";

    // 장소, 병원명
    JsonNode poi = null;
    String nnn = null;

    try {
      response = client.send(requestP, HttpResponse.BodyHandlers.ofString());


      // JSON 파싱
      ObjectMapper mapper = new ObjectMapper();
      JsonNode rootNode = mapper.readTree(response.body());

      // 병원 이름 추출
      JsonNode poisNode = rootNode.path("searchPoiInfo")
              .path("pois")
              .path("poi");
      if (poisNode.isArray() && poisNode.size() > 0) {
        for (int i = 0; i < poisNode.size(); i++) {
          poi = poisNode.get(i);
          nnn = poi.path("name").asText();
          // 모든 공백 제거
          nnn = nnn.replaceAll("\\s+", "");

          // "응급" 포함된 병원만 필터링
          if (nnn.contains("응급")) {
            erHospitals.add(poi);
          }

        }

        if (!erHospitals.isEmpty()) {
          JsonNode nearestER = erHospitals.get(0);
          rawName = nearestER.path("name").asText();
          double endLat = nearestER.path("frontLat").asDouble();
          double endLon = nearestER.path("frontLon").asDouble();

          System.out.println("가장 가까운 응급실: " + rawName + " (" + endLat + ", " + endLon + ")");

          // 👉 이 endLat, endLon을 경로 API에 넣으면 됨
        } else {
          System.out.println("근처에 응급실이 없습니다.");
        }
        hospital = rawName;
      } else {
        System.out.println("검색된 병원이 없습니다.");
        hospital = null;
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

    System.out.println("==> [결과] 병원명: "+ hospital +", ETA: " +time);
  }




  // 전체 구조 리포트 다 가져오기
  @Transactional
  public RescueReportListResponse getEveryRescueReport(){
    log.info("[RescueReport] (확인용) 전체 구조 리포트 조회 시작.");
    // 구조 리포트 다 가져옴.
    List<RescueReport> everyReports = rescueReportRepository.findAll();

    // 그걸 response로 만들고 List 안에 적재.
    List<RescueReportResponse> responseList = everyReports.stream()
        .map(rescueReportMapper::toRescueReportResponse)
        .collect(Collectors.toList());

    log.info("[RescueReport] (확인용) 총 {}개의 전체 구조 리포트 조회 완료.", responseList.size());

    // 그걸 ReportListResponse의 필드에 저장하고 리턴.
    return RescueReportListResponse.builder()
        .rescueReports(responseList)
        .totalCount(responseList.size())
        .build();
  }

  // 대기 중인 구조 리포트 최신순으로 가져오기 (완료여부==false, 최신 생성 일자 순)
  @Transactional
  public RescueReportListResponse getWaitRescueReport(){
    log.info("[RescueReport] 대기 중인 구조 리포트 조회 시작.");
    // 구조 리포트중에 구조여부가 false 인것들을 ETA 오름차순으로 가져옴.
    List<RescueReport> waitedReports = rescueReportRepository.findByIsReceivedFalseOrderByEtaAsc();

    // 그걸 response로 만들고 List 안에 적재.
    List<RescueReportResponse> responseList = waitedReports.stream()
        .map(rescueReportMapper::toRescueReportResponse)
        .collect(Collectors.toList());

    log.info("[RescueReport] 총 {}개의 대기 중인 구조 리포트 조회 시작.", responseList.size());

    // 그걸 ReportListResponse의 필드에 저장하고 리턴.
    return RescueReportListResponse.builder()
        .rescueReports(responseList)
        .totalCount(responseList.size())
        .build();
  }



  // 단일 구조 리포트 가져오기
  @Transactional
  public DetailRescueReportResponse getRescueReport(Long id){
    log.info("[RescueReport] id:{} 구조 리포트 상세 조회 시작.", id);
    // id와 일치하는 구조 리포트 가져옴.
    RescueReport report = rescueReportRepository.findById(id).orElseThrow(() -> new CustomException(
        RescueReportErrorCode.RESCUE_REPORT_NOT_FOUND));

    log.info("[RescueReport] id:{} 구조 리포트 상세 조회 완료.", id);
    return rescueReportMapper.toDetailRescueReportResponse(report);
  }




  // 구조 리포트 완료 처리하는 로직.(isReceived를 true로 해서 저장)
  @Transactional
  public void completeRescueReport(Long rescueReportId) {
    log.info("[RescueReport] id:{} 구조 리포트 완료 처리 시작.", rescueReportId);
    // 1. 구조 리포트 조회
    RescueReport rescueReport = rescueReportRepository.findById(rescueReportId)
        .orElseThrow(() -> new CustomException(RescueReportErrorCode.RESCUE_REPORT_NOT_FOUND));

    // 2. isReceived를 true로 변경
    rescueReport.setIsReceived(true);

    // 3. @Transactional 덕분에 JPA가 자동으로 DB 반영

    log.info("[RescueReport] id:{} 구조 리포트 완료 처리 완료.", rescueReportId);
  }

}
