// src/main/java/com/sku/localism_be/domain/voice/service/VoiceService.java
package com.sku.localism_be.domain.voice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sku.localism_be.domain.voice.dto.response.PostEditResponse;
import com.sku.localism_be.domain.voice.dto.response.TranscribeResponse;
import com.sku.localism_be.domain.voice.entity.Voice;
import com.sku.localism_be.domain.voice.repository.VoiceRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoiceService {

  private final Model model;
  private final ObjectMapper om;
  private final VoiceRecordRepository repo;
  private final PromptService promptService;

  @Value("${vosk.sample-rate:16000}")
  private float targetRate;

  @Transactional
  public TranscribeResponse transcribe(MultipartFile file, String languageHint /*unused*/) {
    if (file == null || file.isEmpty()) throw new IllegalArgumentException("파일이 비어 있습니다.");

    long t0 = System.currentTimeMillis();
    String sttOriginal; // ✅ STT 원문

    // 1) 음성 → 16kHz/mono/16bit 변환 후 Vosk 인식
    try (InputStream rawIn = new BufferedInputStream(file.getInputStream());
        AudioInputStream srcAis = AudioSystem.getAudioInputStream(rawIn)) {

      AudioFormat targetFmt = new AudioFormat(
          AudioFormat.Encoding.PCM_SIGNED, targetRate, 16, 1, 2, targetRate, false);

      try (AudioInputStream pcmAis = AudioSystem.getAudioInputStream(targetFmt, srcAis);
          Recognizer rec = new Recognizer(model, targetRate)) {

        byte[] buf = new byte[4096];
        int n;
        StringBuilder out = new StringBuilder();

        while ((n = pcmAis.read(buf)) > 0) {
          if (rec.acceptWaveForm(buf, n)) {
            String seg = parseVoskTextFlexible(rec.getResult());
            if (!seg.isBlank()) out.append(seg).append(' ');
          }
        }
        String fin = parseVoskTextFlexible(rec.getFinalResult());
        if (!fin.isBlank()) out.append(fin);

        sttOriginal = out.toString().trim(); // ✅ STT 원문
      }
    } catch (UnsupportedAudioFileException e) {
      throw new IllegalArgumentException("지원하지 않는 오디오 형식입니다. WAV/PCM 또는 16kHz mono 16bit로 변환 후 업로드하세요.", e);
    } catch (IOException e) {
      throw new RuntimeException("오디오 스트림 처리 중 오류가 발생했습니다.", e);
    }

    double durationSec = (System.currentTimeMillis() - t0) / 1000.0;
    if (sttOriginal.isBlank()) log.warn("Transcribe 결과가 비어 있습니다.");

    // 2) 교정 & 요약 (네가 요청한 getter 방식)
    PostEditResponse post = promptService.refineAndSummarize(sttOriginal);
    String corrected = (post.getCorrected() != null && !post.getCorrected().isBlank())
        ? post.getCorrected() : sttOriginal;
    String summary = post.getSummary(); // 응답용

    // 3) DB 저장: 원본/교정 분리 저장
    Voice v = new Voice();
    v.setOriginalFilename(file.getOriginalFilename());
    v.setContentType(file.getContentType());
    v.setDurationSec(durationSec);
    v.setOriginalText(sttOriginal); // ✅ 원본 저장
    v.setText(corrected);  // ✅ 교정 저장
    v.setSummary(summary);
    v = repo.save(v);

    // 4) 응답: 원본/교정/요약 모두 포함 (요약은 DB 저장 안 해도 응답 OK)
    return TranscribeResponse.builder()
        .id(v.getId())
        .originalText(v.getOriginalText())
        .text(v.getText())
        .summary(summary)
        .durationSec(v.getDurationSec())
        .createdAt(v.getCreatedAt() != null ? v.getCreatedAt() : LocalDateTime.now())
        .build();
  }

  @Transactional(readOnly = true)
  public List<TranscribeResponse> getAllVoices() {
    return repo.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
        .stream()
        .map(v -> TranscribeResponse.builder()
            .id(v.getId())
            .originalText(v.getOriginalText()) // ✅ 리스트에도 원본/교정 함께 노출
            .text(v.getText())
            .summary(v.getSummary()) // 목록에서는 요약을 생략하거나 별도 API로 제공
            .durationSec(v.getDurationSec())
            .createdAt(v.getCreatedAt())
            .build())
        .toList();
  }

  private String parseVoskTextFlexible(String json) {
    try {
      JsonNode root = om.readTree(json);
      String text = root.path("text").asText("");
      if (!text.isBlank()) return text;
      String partial = root.path("partial").asText("");
      if (!partial.isBlank()) return partial;
      return "";
    } catch (IOException e) {
      log.warn("Vosk JSON 파싱 실패: {}", e.getMessage());
      return "";
    }
  }
}
