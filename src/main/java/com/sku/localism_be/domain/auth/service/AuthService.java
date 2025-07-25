package com.sku.localism_be.domain.auth.service;


import com.sku.localism_be.domain.auth.dto.request.LoginRequest;
import com.sku.localism_be.domain.auth.dto.response.LoginResponse;
import com.sku.localism_be.domain.auth.mapper.AuthMapper;
import com.sku.localism_be.domain.user.entity.User;
import com.sku.localism_be.domain.user.exception.UserErrorCode;
import com.sku.localism_be.domain.user.repository.UserRepository;
import com.sku.localism_be.global.exception.CustomException;
import com.sku.localism_be.global.jwt.JwtProvider;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final JwtProvider jwtProvider;
  private final UserRepository userRepository;
  private final AuthMapper authMapper;

  @Transactional
  public LoginResponse login(LoginRequest loginRequest) {
    log.info("로그인 시도: {}", loginRequest.getUsername());

    User user = userRepository.findByUsername(loginRequest.getUsername())
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
            loginRequest.getPassword());

    // 인증 처리
    authenticationManager.authenticate(authenticationToken);

    // 액세스 토큰 및 리프레시 토큰 발급
    String accessToken = jwtProvider.createAccessToken(user.getUsername());
    String refreshToken = jwtProvider.createRefreshToken(user.getUsername(),
        UUID.randomUUID().toString()); // 보통 refreshToken은 UUID객체 형태로 발급됨.

    // 리프레시 토큰 저장
    user.createRefreshToken(refreshToken);

    // Access Token의 만료 시간을 가져옴
    Long expirationTime = jwtProvider.getExpiration(accessToken);

    // 로그인 성공 로깅
    log.info("로그인 성공: {}", user.getUsername());

    // 로그인 응답 반환
    return authMapper.toLoginResponse(user, accessToken, expirationTime);
  }
}
