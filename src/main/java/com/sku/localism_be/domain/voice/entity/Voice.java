// src/main/java/com/sku/localism_be/domain/voice/entity/Voice.java
package com.sku.localism_be.domain.voice.entity;

import com.sku.localism_be.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "voice_record")
@Getter
@Setter
public class Voice extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String originalFilename;
  private String contentType;

  @Column(nullable = true)
  private Double durationSec;

  @Column(columnDefinition = "TEXT")
  private String originalText; // ✅ STT 원문 (새로 추가)

  @Column(columnDefinition = "TEXT")
  private String text; // ✅ 교정된 문장(기존 text 필드 활용)
  // Voice.java
  @Column(columnDefinition = "TEXT")
  private String summary;
}