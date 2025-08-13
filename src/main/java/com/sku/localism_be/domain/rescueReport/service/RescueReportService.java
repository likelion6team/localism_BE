package com.sku.localism_be.domain.rescueReport.service;


import com.sku.localism_be.domain.detailCard.dto.response.SmallReportListResponse;
import com.sku.localism_be.domain.report.dto.request.ReportRequest;
import com.sku.localism_be.domain.report.dto.response.DetailReportResponse;
import com.sku.localism_be.domain.report.dto.response.PostReportResponse;
import com.sku.localism_be.domain.report.dto.response.ReportListResponse;
import com.sku.localism_be.domain.report.dto.response.ReportResponse;
import com.sku.localism_be.domain.report.entity.Report;
import com.sku.localism_be.domain.report.exception.ReportErrorCode;
import com.sku.localism_be.domain.report.repository.ReportRepository;
import com.sku.localism_be.domain.rescueReport.dto.request.RescueReportRequest;
import com.sku.localism_be.domain.rescueReport.dto.response.DetailRescueReportResponse;
import com.sku.localism_be.domain.rescueReport.dto.response.PostRescueReportResponse;
import com.sku.localism_be.domain.rescueReport.dto.response.RescueReportListResponse;
import com.sku.localism_be.domain.rescueReport.dto.response.RescueReportResponse;
import com.sku.localism_be.domain.rescueReport.entity.RescueReport;
import com.sku.localism_be.domain.rescueReport.exception.RescueReportErrorCode;
import com.sku.localism_be.domain.rescueReport.mapper.RescueReportMapper;
import com.sku.localism_be.domain.rescueReport.repository.RescueReportRepository;
import com.sku.localism_be.global.exception.CustomException;
import com.sku.localism_be.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class RescueReportService {

  private final RescueReportRepository rescueReportRepository;
  private final RescueReportMapper rescueReportMapper;
  private final ReportRepository reportRepository;

  @Transactional
  public PostRescueReportResponse inputRescueReport(RescueReportRequest request) {

    // 음성 인식 로직



    // 병원 예상 시간 로직
    String hospital = "강남베드로병원";
    int time = 7;


    
    // 일치하는 사고 리포트 가져오기
    Report report = reportRepository.findById(request.getReportId()).orElseThrow(() -> new CustomException(
        ReportErrorCode.REPORT_NOT_FOUND));

    // ai 추천 조치
    List<String> recommend = new ArrayList<>();

    // ai 프롬프트


    // 리스트 -> String
    recommend.add("CPR 실시");
    recommend.add("전기 충격");
    recommend.add("가족 연락");

    String r =String.join(",", recommend);




    // DB에 저장
    RescueReport rescueReport = RescueReport.builder()
        .details("음성 준비 중...")
        .hospital(hospital)
        .eta(time)
        .isReceived(false)
        .recommendedResources(r)
        .report(report)
        //.voice(voice)
        .build();


    RescueReport savedReport = rescueReportRepository.save(rescueReport);

    // 해당 신고는 구조 완료 처리. 
    savedReport.getReport().setIsRescue(true);


    return PostRescueReportResponse.builder()
        .id(savedReport.getId())
        .reportId(savedReport.getReport().getId())
        .hospital(savedReport.getHospital())
        .eta(savedReport.getEta())
        .build();

  }





  // 전체 구조 리포트 다 가져오기
  @Transactional
  public RescueReportListResponse getEveryRescueReport(){
    // 구조 리포트 다 가져옴.
    List<RescueReport> everyReports = rescueReportRepository.findAll();

    // 그걸 response로 만들고 List 안에 적재.
    List<RescueReportResponse> responseList = everyReports.stream()
        .map(rescueReportMapper::toRescueReportResponse)
        .collect(Collectors.toList());

    // 그걸 ReportListResponse의 필드에 저장하고 리턴.
    return RescueReportListResponse.builder()
        .rescueReports(responseList)
        .totalCount(responseList.size())
        .build();
  }

  // 대기 중인 구조 리포트 최신순으로 가져오기 (완료여부==false, 최신 생성 일자 순)
  @Transactional
  public RescueReportListResponse getWaitRescueReport(){
    // 구조 리포트중에 구조여부가 false 인것들을 ETA 오름차순으로 가져옴.
    List<RescueReport> waitedReports = rescueReportRepository.findByIsReceivedFalseOrderByEtaAsc();

    // 그걸 response로 만들고 List 안에 적재.
    List<RescueReportResponse> responseList = waitedReports.stream()
        .map(rescueReportMapper::toRescueReportResponse)
        .collect(Collectors.toList());

    // 그걸 ReportListResponse의 필드에 저장하고 리턴.
    return RescueReportListResponse.builder()
        .rescueReports(responseList)
        .totalCount(responseList.size())
        .build();
  }



  // 단일 구조 리포트 가져오기
  @Transactional
  public DetailRescueReportResponse getRescueReport(Long id){
    // id와 일치하는 구조 리포트 가져옴.
    RescueReport report = rescueReportRepository.findById(id).orElseThrow(() -> new CustomException(
        RescueReportErrorCode.RESCUE_REPORT_NOT_FOUND));

    return rescueReportMapper.toDetailRescueReportResponse(report);
  }




  // 사고 리포트 완료 처리하는 로직.(isReceived를 true로 해서 저장)
  @Transactional
  public void completeRescueReport(Long rescueReportId) {
    // 1. 구조 리포트 조회
    RescueReport rescueReport = rescueReportRepository.findById(rescueReportId)
        .orElseThrow(() -> new CustomException(RescueReportErrorCode.RESCUE_REPORT_NOT_FOUND));

    // 2. isReceived를 true로 변경
    rescueReport.setIsReceived(true);

    // 3. @Transactional 덕분에 JPA가 자동으로 DB 반영
  }

}
