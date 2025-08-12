package com.sku.localism_be.domain.report.service;


import com.sku.localism_be.domain.detailCard.dto.response.SmallReportListResponse;
import com.sku.localism_be.domain.detailCard.dto.response.SmallReportResponse;
import com.sku.localism_be.domain.detailCard.entity.DetailCard;
import com.sku.localism_be.domain.report.dto.request.ReportRequest;
import com.sku.localism_be.domain.report.dto.response.BasicReportResponse;
import com.sku.localism_be.domain.report.dto.response.ReportListResponse;
import com.sku.localism_be.domain.report.dto.response.ReportResponse;
import com.sku.localism_be.domain.report.entity.Report;
import com.sku.localism_be.domain.report.mapper.ReportMapper;
import com.sku.localism_be.domain.report.repository.ReportRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {

  private final ReportRepository reportRepository;
  private final ReportMapper reportMapper;

  // private final String uploadDir = "/home/ubuntu/uploads";
  private final String uploadDir = "C:/uploadRESQ/";

  // Post 리포트
  @Transactional
  public BasicReportResponse inputReport(ReportRequest request, MultipartFile img) {
    String photoPath = null;

    MultipartFile image = img;
    if (image != null && !image.isEmpty()) {
      try {
        Path uploadPath = Paths.get(uploadDir);

        // 디렉토리 생성도 try 블록 안에 넣기
        if (Files.notExists(uploadPath)) {
          Files.createDirectories(uploadPath);
        }

        String originalFilename = image.getOriginalFilename();
        String ext = "";

        if (originalFilename != null && originalFilename.contains(".")) {
          ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String filename = UUID.randomUUID().toString() + ext;
        Path filePath = uploadPath.resolve(filename);

        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        photoPath = filePath.toString();

      } catch (IOException e) {
        log.error("이미지 저장 실패", e);
        throw new RuntimeException("이미지 저장 중 오류가 발생했습니다.");
      }
    }

    // DB에 저장
    Report report = Report.builder()
        .reporter(request.getReporter())
        .location(request.getLocation())
        .lat(request.getLat())
        .lng(request.getLng())
        // created는 @CreationTimestamp로 자동 생성하므로 request.getCreated()는 무시하거나 사용 안 해도 됨
        .consciousnessStatus(request.getConsciousnessStatus())
        // 리스트 필드인 accidentType, mainSymptoms는 comma-separated로 변환
        .accidentType(String.join(",", request.getAccidentType()))
        .mainSymptoms(String.join(",", request.getMainSymptoms()))
        .breathingStatus(request.getBreathingStatus())
        .bleedingLevel(request.getBleedingLevel())
        .medicalHistory(request.getMedicalHistory())
        .photoPath(photoPath)
        .isRescue(request.getIsRescue())
        .caseId(UUID.randomUUID().toString())  // 케이스 ID는 임의 생성 (필요에 따라 조정)
        .build();


    Report savedReport = reportRepository.save(report);


      // id 얻어서 response에 넣고 return
    return BasicReportResponse.builder()
        .id(savedReport.getId())
        .build();


  }


  // 전체 신고 리포트 다 가져오기
  @Transactional
  public ReportListResponse getEveryReport(){
    // 신고 리포트 다 가져옴.
    List<Report> everyReports = reportRepository.findAll();

    // 그걸 response로 만들고 List 안에 적재.
    List<ReportResponse> responseList = everyReports.stream()
        .map(reportMapper::toReportResponse)
        .collect(Collectors.toList());

    // 그걸 ReportListResponse의 필드에 저장하고 리턴.
    return ReportListResponse.builder()
        .reports(responseList)
        .totalCount(responseList.size())
        .build();
  }

  // 대기 중인 신고 리포트 최신순으로 가져오기 (구조여부==false, 최신 생성 일자 순)
  @Transactional
  public ReportListResponse getWaitReport(){
    // 신고 리포트중에 구조여부가 false 인것들을 생성일자 오름차순으로 가져옴.
    List<Report> waitedReports = reportRepository.findByIsRescueFalseOrderByCreatedDesc();

    // 그걸 response로 만들고 List 안에 적재.
    List<ReportResponse> responseList = waitedReports.stream()
        .map(reportMapper::toReportResponse)
        .collect(Collectors.toList());

    // 그걸 ReportListResponse의 필드에 저장하고 리턴.
    return ReportListResponse.builder()
        .reports(responseList)
        .totalCount(responseList.size())
        .build();
  }

}
