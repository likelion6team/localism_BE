package com.sku.localism_be.domain.detailCard.repository;

import com.sku.localism_be.domain.detailCard.entity.DetailCard;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetailCardRepository extends JpaRepository<DetailCard, Long>  {

  List<DetailCard> findByYearAndMonthAndDay(Integer year, Integer month, Integer day);

  List<DetailCard> findByYearAndMonthAndDayAndHourAndMinuteOrderByYearDescMonthDescDayDescHourDescMinuteDesc(
      Integer year, Integer month, Integer day, Integer hour, Integer minute
  );


}
