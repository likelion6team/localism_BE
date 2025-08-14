package com.sku.localism_be.domain.rescueReport.exception;


import com.sku.localism_be.global.exception.model.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RescueReportErrorCode implements BaseErrorCode {
  RESCUE_REPORT_ALREADY_EXISTS("RESCUE_REPORT_4001", "이미 존재하는 사고 리포트 아이디입니다.", HttpStatus.BAD_REQUEST),
  RESCUE_REPORT_NOT_FOUND("RESCUE_REPORT_4002", "존재하지 않는 사고 리포트 데이터입니다.", HttpStatus.NOT_FOUND),
  RESCUE_REPORT_API_ERROR("RESCUE_REPORT_4003", "장소 검색 API 에러입니다.", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
