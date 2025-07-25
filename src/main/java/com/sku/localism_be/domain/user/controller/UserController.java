package com.sku.localism_be.domain.user.controller;


import com.sku.localism_be.domain.user.dto.request.SignUpRequest;
import com.sku.localism_be.domain.user.dto.response.SignUpResponse;
import com.sku.localism_be.domain.user.dto.response.UserResponse;
import com.sku.localism_be.domain.user.service.UserService;
import com.sku.localism_be.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name="User", description="User 관리 API")
public class UserController {

  private final UserService userService;

  @Operation(summary="회원가입 API", description ="사용자 회원가입을 위한 API")
  @PostMapping("/sign-up")
  public ResponseEntity<BaseResponse<SignUpResponse>> signup(
      @RequestBody @Valid SignUpRequest signUpRequest) {
    SignUpResponse signUpResponse = userService.signUp(signUpRequest);
    return ResponseEntity.ok(BaseResponse.success("회원가입에 성공했습니다.", signUpResponse));
  }

  @Operation(summary = "회원 전체 조회",
      description = "전체 회원 정보를 조회하는 API.")
  @GetMapping("/users")
  public ResponseEntity<BaseResponse<List<UserResponse>>> getAllUsers() {
    List<UserResponse> response = userService.getAllUsers();
    return ResponseEntity.ok(BaseResponse.success("전체 회원 조회 성공", response));
  }

}
