package com.sku.localism_be.domain.user.service;

import com.sku.localism_be.domain.user.dto.request.SignUpRequest;
import com.sku.localism_be.domain.user.dto.response.SignUpResponse;
import com.sku.localism_be.domain.user.dto.response.UserResponse;
import com.sku.localism_be.domain.user.entity.User;
import com.sku.localism_be.domain.user.exception.UserErrorCode;
import com.sku.localism_be.domain.user.mapper.UserMapper;
import com.sku.localism_be.domain.user.repository.UserRepository;
import com.sku.localism_be.global.exception.CustomException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public SignUpResponse signUp(SignUpRequest request) {
    log.info("회원가입 시도: {}", request.getUsername());
    
    // 1. 입력된 아이디로 회원이 있는지 확인. 있으면 에러
    if(userRepository.existsByUsername(request.getUsername())) {
      throw new CustomException(UserErrorCode.USERNAME_ALREADY_EXISTS);
    }

    // 2. 비번 암호화
    String encodedPassword = passwordEncoder.encode(request.getPassword());

    // 3. 암호화된 비번과 받은 정보로 엔티티를 만듦.
    User user = User.builder()
        .username(request.getUsername())
        .password(encodedPassword)
        .name(request.getName())
        .language(request.getLanguage())
        .introduce(request.getIntroduce())
        .build();

    // 4. User 엔티티로 레포지토리에 접근(저장)
    User savedUser = userRepository.save(user);
    log.info("회원가입 성공: {}", savedUser.getUsername());

    return userMapper.toSignUpResponse(savedUser);
  }



  // 회원 전체 조회
  @Transactional
  public List<UserResponse> getAllUsers() {
    
    List<User> userList = userRepository.findAll();
    log.info("(회원 전체 조회 : 현재 {}명 가입중)", userList.size());
    return userList.stream().map(userMapper::toUserResponse).toList();
  }

}
