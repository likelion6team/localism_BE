package com.sku.localism_be.domain.detailCard.service;


import com.sku.localism_be.domain.detailCard.dto.request.InputReportRequest;
import com.sku.localism_be.domain.detailCard.dto.request.SmallReportDateRequest;
import com.sku.localism_be.domain.detailCard.dto.request.SmallReportLatestRequest;
import com.sku.localism_be.domain.detailCard.dto.response.InputReportResponse;
import com.sku.localism_be.domain.detailCard.dto.response.SmallReportListResponse;
import com.sku.localism_be.domain.detailCard.dto.response.SmallReportResponse;
import com.sku.localism_be.domain.detailCard.entity.DetailCard;
import com.sku.localism_be.domain.detailCard.mapper.DetailCardMapper;
import com.sku.localism_be.domain.detailCard.repository.DetailCardRepository;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CardService {

  private final DetailCardRepository detailCardRepository;
  private final DetailCardMapper detailCardMapper;


  // 최신순
  @Transactional
  public SmallReportListResponse getLatest(List<Long> ids){

    // repository와 비교하려 최신 순으로 내림차순으로 정렬된 5개를 가져옴.
    List<DetailCard> latestReports = detailCardRepository.findTop5ByIdInOrderByYearDescMonthDescDayDescHourDescMinuteDesc(ids);
    System.out.println("latestReports" + latestReports);

    // 5개 이용해 각각 SmallCardResponse 객체를 만들고 그 결과를 List<SmallCardResponse> 안에 적재.
    List<SmallReportResponse> responseList = latestReports.stream()
        .map(detailCardMapper::toSmallReportResponse)
        .collect(Collectors.toList());


    // 그걸 SmallReportListResponse의 필드에 저장하고 리턴.
    return SmallReportListResponse.builder()
        .reportList(responseList)
        .build();
    
  }


  // 날짜별
  @Transactional
  public SmallReportListResponse getDate(List<Long> ids, int year, int month, int day) {

    if (ids == null || ids.isEmpty()) {
      return SmallReportListResponse.builder()
          .reportList(Collections.emptyList())
          .build();
    }


    // id들 작성일과 선택된 날짜가 일치하는 리스트를 가지고 옴.
    List<DetailCard> dateReports = detailCardRepository.findByIdInAndYearAndMonthAndDay(
        ids, year, month, day
    );


    // 받은 리포트 정보를 바탕으로 요약 리포트를 만들어서 저장.
    List<SmallReportResponse> responseList = dateReports.stream()
        .map(detailCardMapper::toSmallReportResponse)
        .collect(Collectors.toList());


    // 그걸 SmallReportListResponse의 필드에 저장하고 리턴.
    return SmallReportListResponse.builder()
        .reportList(responseList)
        .build();

  }

}
