// src/main/java/com/sku/localism_be/domain/voice/service/PromptService.java
package com.sku.localism_be.domain.voice.service;

import com.sku.localism_be.domain.voice.dto.response.PostEditResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PromptService {

  @Value("${openai.api-url:https://api.openai.com/v1/chat/completions}")
  private String apiUrl;

  // ✅ 하이픈/닷 두 표기 모두 지원 + 기본값 빈 문자열
  @Value("${openai.api-key:${openai.api.key:}}")
  private String apiKey;

  @Value("${openai.model:gpt-4o-mini}")
  private String model;

  private final WebClient web = WebClient.builder().build();

  public PostEditResponse refineAndSummarize(String text) {
    if (apiKey == null || apiKey.isBlank()) {
      log.warn("OPENAI API KEY missing. Skip LLM refine/summarize.");
      return new PostEditResponse(text, "");
    }

    String prompt = """
    다음 문장은 구급대원이 현장에서 말한 음성을 STT로 변환한 텍스트입니다.

    1. 원문과 의미를 100%% 유지하면서 오타, 띄어쓰기, 맞춤법 오류를 수정하세요.
    2. 응급 의료 분야 표준 용어로 교정하세요. (예: 심장 압박 → 흉부 압박술, 산소호흡기 → 산소 투여)
    3. 원래 의미를 유지한 상태에서, 병원 인수인계에 적합하도록 간결하게 두 문장 정도로 요약하세요.
    4. 요약에는 현장에서 시행한 응급 처치 또는 의료 조치가 자연스럽게 포함되도록 하세요.
    5. 불필요한 추측이나 원문에 없는 내용은 추가하지 마세요.

    문장: "%s"

    출력 형식:
    - 교정된 문장: (오타 및 띄어쓰기, 용어 교정 후 문장)
    - 요약: (병원 인수인계용 두 문장 요약, 응급 조치 포함)
    """.formatted(text);

    Map<String, Object> body = Map.of(
        "model", model,
        "messages", List.of(
            Map.of("role","system","content","너는 한국어 의료 문서 교정/요약 전문가다."),
            Map.of("role","user","content", prompt)
        ),
        "temperature", 0.2
    );

    try {
      Map resp = web.post()
          .uri(apiUrl)
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
          .contentType(MediaType.APPLICATION_JSON)
          .body(BodyInserters.fromValue(body))
          .retrieve()
          .bodyToMono(Map.class)
          .block();

      var choices = (List<Map<String,Object>>) resp.get("choices");
      var message = (Map<String,Object>) choices.get(0).get("message");
      String content = (String) message.get("content");

      String corrected = unquote(extractAfter(content, "- 교정된 문장:"));
      String summary   = unquote(extractAfter(content, "- 요약:"));
      if (corrected.isBlank()) corrected = text; // 방어

      return new PostEditResponse(corrected, summary);
    } catch (Exception e) {
      log.warn("LLM 호출 실패: {}", e.getMessage());
      return new PostEditResponse(text, "");
    }
  }

  private String extractAfter(String src, String marker) {
    if (src == null) return "";
    for (String line : src.split("\\r?\\n")) {
      String t = line.trim();
      if (t.startsWith(marker)) {
        return t.substring(marker.length()).trim();
      }
    }
    return "";
  }

  // ✅ 따옴표/스마트 따옴표 제거
  private String unquote(String s) {
    if (s == null) return "";
    s = s.trim();
    if (s.length() >= 2) {
      char a = s.charAt(0), b = s.charAt(s.length()-1);
      if ((a=='"' && b=='"') || (a=='“' && b=='”') || (a=='\'' && b=='\'')) {
        return s.substring(1, s.length()-1).trim();
      }
    }
    return s;
  }
}
