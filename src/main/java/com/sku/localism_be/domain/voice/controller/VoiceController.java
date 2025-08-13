package com.sku.localism_be.domain.voice.controller;

import com.sku.localism_be.domain.voice.dto.response.TranscribeResponse;
import com.sku.localism_be.domain.voice.service.VoiceService;
import com.sku.localism_be.global.common.ByteArrayMultipartFile;
import com.sku.localism_be.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/voice")
@Tag(name = "Voice", description = "음성 녹음(STT) API")
public class VoiceController {

  private static final Logger log = LoggerFactory.getLogger(VoiceController.class);
  private final VoiceService service;

  // 1) multipart/form-data 업로드
  @Operation(summary = "음성 녹음 (multipart)", description = "multipart/form-data로 업로드한 음성을 텍스트로 전환합니다.")
  @PostMapping(
      value = "/transcribe",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<BaseResponse<TranscribeResponse>> transcribe(
      @RequestPart("file") MultipartFile file
  ) {
    log.info("HIT /transcribe (multipart) name={}, size={}",
        file.getOriginalFilename(), file.getSize());

    // language 제거했으니 null 넘겨줌
    TranscribeResponse res = service.transcribe(file, null);

    return ResponseEntity.ok(BaseResponse.success("전환에 성공했습니다.", res));
  }

  // 2) application/octet-stream 업로드
  @Operation(
      summary = "음성 녹음 (바이트 스트림)",
      description = "application/octet-stream(바이너리 바디)로 전송된 오디오를 받아 전사합니다."
  )
  @PostMapping(
      value = "/transcribe-bytes",
      consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<BaseResponse<TranscribeResponse>> transcribeBytes(
      @RequestBody byte[] data,
      @RequestHeader(value = "X-Filename", required = false) String filename,
      @RequestParam(value = "language", required = false) String language
  ) {
    log.info("HIT /transcribe-bytes (octet-stream) filename={}, size={}, language={}",
        filename, (data == null ? 0 : data.length), language);

    String safeName = (filename == null || filename.isBlank()) ? "audio" : filename;
    MultipartFile file = new ByteArrayMultipartFile(data, "file", safeName, MediaType.APPLICATION_OCTET_STREAM_VALUE);

    TranscribeResponse res = service.transcribe(file, language);
    return ResponseEntity.ok(BaseResponse.success("전환에 성공했습니다.", res));
  }
  // 전체 녹음 리스트 조회
  @Operation(summary = "녹음 전체 조회", description = "저장된 모든 음성 전사 기록을 조회합니다.")
  @GetMapping("/list")
  public ResponseEntity<BaseResponse<List<TranscribeResponse>>> getAllVoices() {
    List<TranscribeResponse> res = service.getAllVoices();
    return ResponseEntity.ok(BaseResponse.success("조회에 성공했습니다.", res));
  }

  // 👉 GET /{id}, /latest는 기존 그대로 두셔도 됩니다(저장 로직 붙인 뒤 사용).
}
