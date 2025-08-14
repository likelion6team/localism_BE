package com.sku.localism_be.domain.vitalSign.entity;


import com.sku.localism_be.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vitalSign")
public class VitalSign extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 수축기 혈압
    private Integer systolic10;
    private Integer systolic8;
    private Integer systolic6;
    private Integer systolic4;
    private Integer systolic2;
    private Integer systolic0; // 현재

    // 이완기 혈압
    private Integer diastolic10;
    private Integer diastolic8;
    private Integer diastolic6;
    private Integer diastolic4;
    private Integer diastolic2;
    private Integer diastolic0;

    // 맥박
    private Integer pulse10;
    private Integer pulse8;
    private Integer pulse6;
    private Integer pulse4;
    private Integer pulse2;
    private Integer pulse0;

    // 호흡수
    private Integer respiration;

    // 산소포화도
    private Integer spo2;


}
