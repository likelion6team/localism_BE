package com.sku.localism_be.domain.report.service;


import com.sku.localism_be.domain.report.dto.request.ReportRequest;
import com.sku.localism_be.domain.report.dto.response.PostReportResponse;
import com.sku.localism_be.domain.report.dto.response.DetailReportResponse;
import com.sku.localism_be.domain.report.dto.response.ReportListResponse;
import com.sku.localism_be.domain.report.dto.response.ReportResponse;
import com.sku.localism_be.domain.report.entity.Report;
import com.sku.localism_be.domain.report.exception.ReportErrorCode;
import com.sku.localism_be.domain.report.mapper.ReportMapper;
import com.sku.localism_be.domain.report.repository.ReportRepository;
import com.sku.localism_be.global.exception.CustomException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {

  private final ReportRepository reportRepository;
  private final ReportMapper reportMapper;

  //private final String uploadDir = "/home/ubuntu/hackaton/uploads";
  private final String uploadDir = "C:/uploadRESQ/";

  // Post 리포트
  @Transactional
  public PostReportResponse inputReport(ReportRequest request, MultipartFile img) {
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
        .location(request.getLocation())
        .lat(request.getLat())
        .lng(request.getLng())
        // created는 @CreationTimestamp로 자동 생성하므로 request.getCreated()는 무시하거나 사용 안 해도 됨
        .consciousnessStatus(request.getConsciousnessStatus())
        // 리스트 필드인 accidentType, mainSymptoms는 comma-separated로 변환
        .accidentType(String.join(",", request.getAccidentType()))
        .mainSymptoms(String.join(",", request.getMainSymptoms()))
        .breathingStatus(request.getBreathingStatus())
        .photoPath(photoPath)
        .isRescue(false)
        .build();


    Report savedReport = reportRepository.save(report);
    return reportMapper.toPostReportResponse(savedReport);
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


  // 단일 신고 리포트 가져오기
  @Transactional
  public DetailReportResponse getReport(Long id){
    // id와 일치하는 신고 리포트 가져옴.
    Report report = reportRepository.findById(id).orElseThrow(() -> new CustomException(
        ReportErrorCode.REPORT_NOT_FOUND));

    return reportMapper.toDetailReportResponse(report);
  }


  // 사진 파일 리턴하기
  @Transactional(readOnly = true)
  public Resource getReportImage(Long id) throws MalformedURLException {
    Report report = reportRepository.findById(id)
        .orElseThrow(() -> new CustomException(ReportErrorCode.REPORT_NOT_FOUND));

    String path = report.getPhotoPath();
    if (path == null) {
      throw new CustomException(ReportErrorCode.IMAGE_NOT_FOUND);
    }

    Path filePath = Paths.get(path).toAbsolutePath();

    if (!Files.exists(filePath)) {
      throw new CustomException(ReportErrorCode.IMAGE_NOT_FOUND);
    }

    return new UrlResource(filePath.toUri());
  }

}
