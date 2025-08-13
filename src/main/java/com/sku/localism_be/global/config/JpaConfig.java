package com.sku.localism_be.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing // ✅ JPA Auditing 기능 켜기
public class JpaConfig {
}
