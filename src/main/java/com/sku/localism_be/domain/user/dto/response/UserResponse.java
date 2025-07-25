package com.sku.localism_be.domain.user.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title="UserResponse DTO", description = "회원 조회에 대한 응답 변환")
public class UserResponse {
  @Schema(description = "요청된 사용자 ID", example = "1")
  private Long userId;

  @Schema(description = "요청된 사용자 아이디", example = "heejun0109@naver.com")
  private String username;

  @Schema(description = "요청된 사용자 이름(별명)", example = "아이러브스파이시")
  private String name;

  @Schema(description = "요청된 사용자 언어 선택", example = "English")
  private String language;

  @Schema(description = "요청된 사용자 자기 소개", example = "Hello!")
  private String introduce;

}
