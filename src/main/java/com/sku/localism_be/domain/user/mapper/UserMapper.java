package com.sku.localism_be.domain.user.mapper;

import com.sku.localism_be.domain.user.dto.response.SignUpResponse;
import com.sku.localism_be.domain.user.dto.response.UserResponse;
import com.sku.localism_be.domain.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  public SignUpResponse toSignUpResponse(User user) {
    return SignUpResponse.builder()
        .userId(user.getId())
        .username(user.getUsername())
        .build();
  }

  public UserResponse toUserResponse(User user) {
    return UserResponse.builder()
        .userId(user.getId())
        .username(user.getUsername())
        .name(user.getName())
        .language(user.getLanguage())
        .introduce(user.getIntroduce())
        .build();
  }

}
