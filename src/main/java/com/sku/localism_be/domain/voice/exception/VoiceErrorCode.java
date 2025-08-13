package com.sku.localism_be.domain.voice.exception;

import com.sku.localism_be.global.exception.model.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum VoiceErrorCode implements BaseErrorCode {
  VOICE_NOT_FOUND("VOICE_4041", "전사 기록을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  VOICE_UNSUPPORTED_AUDIO("VOICE_4001", "지원하지 않는 오디오 형식입니다.", HttpStatus.BAD_REQUEST),
  VOICE_STT_FAILED("VOICE_5001", "음성 인식 처리에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String code;
  private final String message;
  private final HttpStatus status;
}