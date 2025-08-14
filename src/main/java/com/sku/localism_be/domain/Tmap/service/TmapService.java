package com.sku.localism_be.domain.Tmap.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sku.localism_be.domain.Tmap.dto.response.TmapNearestResponse;
import com.sku.localism_be.domain.report.dto.request.ReportRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Slf4j
@Service
public class TmapService {

  private final WebClient tmapWebClient;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public TmapService(@Qualifier("tmapWebClient") WebClient tmapWebClient) {
    this.tmapWebClient = tmapWebClient;
  }

  /** ReportRequest(lat/lng)로 조회 */
  public TmapNearestResponse findNearestFromReport(ReportRequest reportReq) {
    if (reportReq.getLat() == null || reportReq.getLng() == null) {
      throw new IllegalArgumentException("ReportRequest.lat/lng가 필요합니다.");
    }
    return findNearest(reportReq.getLat(), reportReq.getLng());
  }

  /** 위경도 직접 조회: 병원만 검색 (없으면 키워드·반경 단계적 확장) */
  public TmapNearestResponse findNearest(double lat, double lon) {
    // 1차: 2km
    Map<String, Object> poi = searchNearestHospital(lat, lon, /*radiusMeters*/1500);

    // 2차: 없으면 5km로 재시도
    if (poi == null) {
      poi = searchNearestHospital(lat, lon, /*radiusMeters*/5000);
    }

    if (poi == null) {
      return TmapNearestResponse.builder()
          .category("N/A")
          .name("주변에 병원 없음")
          .etaMinutes(0)
          .build();
    }

    int etaSec = fetchDrivingEtaSeconds(lat, lon, poi);
    return TmapNearestResponse.builder()
        .category("병원")
        .name(str(poi.get("name")))
        .etaMinutes((etaSec + 59) / 60)
        .build();
  }

  // ---------- 내부 로직 ----------

  /** 병원 계열 키워드 우선순위로 검색 → 가장 가까운 1개 반환 */
  private Map<String, Object> searchNearestHospital(double lat, double lon, int radiusMeters) {
    // 우선순위: 일반 "병원" → "의원" → "종합병원" → "대학병원" → "의료원" → "의료센터"
    List<String> keywords = List.of("병원", "의원", "종합병원", "대학병원", "의료원", "의료센터");
    for (String kw : keywords) {
      Map<String, Object> poi = searchNearestByKeyword(kw, lat, lon, radiusMeters);
      if (poi != null) return poi;
    }
    return null;
  }

  /** POI 통합검색: radius는 km(정수), searchKeyword는 필수 */
  @SuppressWarnings("unchecked")
  private Map<String, Object> searchNearestByKeyword(String keyword, double lat, double lon, int radiusMeters) {
    int radiusKm = Math.max(1, (int) Math.ceil(radiusMeters / 1000.0)); // 1500m -> 2km

    String poisUri = UriComponentsBuilder.fromPath("/tmap/pois")
        .queryParam("version", 1)
        .queryParam("format", "json")
        .queryParam("searchKeyword", keyword)   // ✅ 핵심: 키워드
        .queryParam("searchType", "all")        // 이름/주소/업종 전체
        .queryParam("searchtypCd", "R")         // 거리순
        .queryParam("centerLon", lon)
        .queryParam("centerLat", lat)
        .queryParam("radius", radiusKm)         // km (정수)
        .queryParam("page", 1)
        .queryParam("count", 20)
        .toUriString();

    log.info("[Tmap GET] {}", poisUri);
    String raw = getWithBodyOrThrow(poisUri);
    Map<String, Object> body = jsonToMap(raw);

    Object poiNode = Optional.ofNullable(body)
        .map(b -> (Map<String, Object>) b.get("searchPoiInfo"))
        .map(info -> info.get("pois"))
        .map(pois -> ((Map<String, Object>) pois).get("poi"))
        .orElse(null);

    List<Map<String, Object>> list =
        (poiNode instanceof List<?> l) ? (List<Map<String, Object>>) l :
            (poiNode instanceof Map) ? List.of((Map<String, Object>) poiNode) :
                List.of();

    return list.isEmpty() ? null : list.get(0); // 거리순 최상위
  }

  /** 자동차 경로 총 소요시간(초) */
  @SuppressWarnings("unchecked")
  private int fetchDrivingEtaSeconds(double originLat, double originLon, Map<String, Object> poi) {
    double destLon = toDouble(poi.get("frontLon") != null ? poi.get("frontLon") : poi.get("noorX"));
    double destLat = toDouble(poi.get("frontLat") != null ? poi.get("frontLat") : poi.get("noorY"));

    String routeUri = UriComponentsBuilder.fromPath("/tmap/routes")
        .queryParam("version", 1)
        .queryParam("format", "json")
        .toUriString();

    Map<String, Object> payload = Map.of(
        "startX", originLon, "startY", originLat,
        "endX",   destLon,   "endY",   destLat,
        "reqCoordType", "WGS84GEO",
        "resCoordType", "WGS84GEO",
        "trafficInfo",  "N"
    );

    log.info("[Tmap POST] {}", routeUri);
    String raw = postJsonWithBodyOrThrow(routeUri, payload);
    Map<String, Object> res = jsonToMap(raw);

    try {
      var features = (List<Map<String,Object>>) res.get("features");
      var props    = (Map<String,Object>) features.get(0).get("properties");
      return toInt(props.get("totalTime")); // 초
    } catch (Exception e) {
      log.warn("[Tmap ROUTE] 응답 파싱 실패: {}", raw, e);
      return 0;
    }
  }

  // ---------- WebClient 래퍼(빈 바디 방어) ----------

  private String getWithBodyOrThrow(String pathAndQuery) {
    return tmapWebClient.get()
        .uri(pathAndQuery)
        .accept(MediaType.APPLICATION_JSON)
        .exchangeToMono(resp -> resp.bodyToMono(String.class).defaultIfEmpty("")
            .map(body -> {
              log.info("[Tmap GET] status={} length={}", resp.statusCode(), (body == null ? 0 : body.length()));
              if (resp.statusCode().isError()) {
                log.error("[Tmap GET] {} {} \n{}", resp.statusCode().value(), resp.statusCode(), body);
                throw new RuntimeException("Tmap GET " + resp.statusCode().value() + ": " + body);
              }
              if (body == null || body.isBlank()) {
                log.warn("[Tmap GET] empty body. Treat as no-result.");
                // 안전 빈결과 JSON
                return "{\"searchPoiInfo\":{\"pois\":{\"poi\":[]}}}";
              }
              // 바디 앞부분 샘플
              log.debug("[Tmap GET] body.sample={}", body.substring(0, Math.min(300, body.length())));
              return body;
            }))
        .block();
  }

  private String postJsonWithBodyOrThrow(String pathAndQuery, Object json) {
    return tmapWebClient.post()
        .uri(pathAndQuery)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .bodyValue(json)
        .exchangeToMono(resp -> resp.bodyToMono(String.class).defaultIfEmpty("")
            .map(body -> {
              log.info("[Tmap POST] status={} length={}", resp.statusCode(), (body == null ? 0 : body.length()));
              if (resp.statusCode().isError()) {
                log.error("[Tmap POST] {} {} \n{}", resp.statusCode().value(), resp.statusCode(), body);
                throw new RuntimeException("Tmap POST " + resp.statusCode().value() + ": " + body);
              }
              if (body == null || body.isBlank()) {
                throw new RuntimeException("Tmap POST empty body (status=" + resp.statusCode() + ")");
              }
              log.debug("[Tmap POST] body.sample={}", body.substring(0, Math.min(300, body.length())));
              return body;
            }))
        .block();
  }

  // ---------- utils ----------

  private Map<String, Object> jsonToMap(String raw) {
    try {
      return objectMapper.readValue(raw, new TypeReference<>() {});
    } catch (Exception e) {
      log.error("[Tmap JSON] parse failed. sample(0..200)={}", raw == null ? "null" : (raw.length() > 200 ? raw.substring(0, 200) : raw));
      throw new RuntimeException("JSON 파싱 실패", e);
    }
  }

  private static String str(Object o) { return o == null ? "" : String.valueOf(o); }
  private static double toDouble(Object o) {
    if (o == null) return 0.0;
    if (o instanceof Number n) return n.doubleValue();
    try { return Double.parseDouble(String.valueOf(o)); } catch (Exception e) { return 0.0; }
  }
  private static int toInt(Object o) {
    if (o == null) return 0;
    if (o instanceof Number n) return n.intValue();
    try { return Integer.parseInt(String.valueOf(o)); } catch (Exception e) { return 0; }
  }
}
