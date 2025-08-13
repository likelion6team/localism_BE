package com.sku.localism_be.domain.detailCard.service;


import com.sku.localism_be.domain.detailCard.dto.response.CalendarCountResponse;
import com.sku.localism_be.domain.detailCard.dto.response.SmallReportListResponse;
import com.sku.localism_be.domain.detailCard.mapper.DetailCardMapper;
import com.sku.localism_be.domain.detailCard.repository.DetailCardRepository;
import java.time.YearMonth;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CalendarService {

  private final DetailCardRepository detailCardRepository;
  private final DetailCardMapper detailCardMapper;

  @Transactional
  public List<CalendarCountResponse> getCountOnMonth(List<Long> ids, int year, int month) {
    // 해당 달에 며칠까지 있는지 알아냄.
    YearMonth yearMonthObj = YearMonth.of(year, month);
    int lastDay = yearMonthObj.lengthOfMonth();


    // 반복문으로 돌려서 개수 찾고 년, 월, 일 정보와 함께 response를 만들어서 List안에 넣기.
    List<CalendarCountResponse> result = new ArrayList<>();

    for (int day = 1; day <= lastDay; day++) {
      Long count = detailCardRepository.countByYearAndMonthAndDay(year, month, day);
      CalendarCountResponse response = new CalendarCountResponse(year, month, day, count);
      result.add(response); // 리스트 뒤에 추가
    }



    // List를 리턴해줌.
    return result;
  }


}
