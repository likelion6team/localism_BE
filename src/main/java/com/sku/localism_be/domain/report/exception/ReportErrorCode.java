package com.sku.localism_be.domain.report.exception;


import com.sku.localism_be.global.exception.model.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReportErrorCode implements BaseErrorCode {
  REPORT_ALREADY_EXISTS("REPORT_4001", "이미 존재하는 구조 리포트 아이디입니다.", HttpStatus.BAD_REQUEST),
  REPORT_NOT_FOUND("REPORT_4002", "존재하지 않는 구조 리포트 데이터입니다.", HttpStatus.NOT_FOUND),
  IMAGE_NOT_FOUND("REPORT_4003", "이미지 파일이 존재하지 않습니다.", HttpStatus.NOT_FOUND);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
