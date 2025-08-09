package com.sku.localism_be.domain.detailCard.exception;

import com.sku.localism_be.global.exception.model.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
@AllArgsConstructor
public enum DetailCardErrorCode implements BaseErrorCode {
  DETAILCARD_ALREADY_EXISTS("DETAILCARD_4001", "이미 존재하는 리포트 카드 아이디입니다.", HttpStatus.BAD_REQUEST),
  DETAILCARD_NOT_FOUND("DETAILCARD_4002", "존재하지 않는 리포트 카드 데이터입니다.", HttpStatus.NOT_FOUND);


  private final String code;
  private final String message;
  private final HttpStatus status;

}
