package com.sku.localism_be.domain.voice.repository;

import com.sku.localism_be.domain.voice.entity.Voice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoiceRecordRepository extends JpaRepository<Voice, Long> {
  List<Voice> findTop5ByOrderByCreatedAtDesc();
}