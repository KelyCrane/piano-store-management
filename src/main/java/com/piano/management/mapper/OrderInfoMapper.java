package com.piano.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.piano.management.entity.OrderInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {
    List<OrderInfo> selectPageWithNames(@Param("param") OrderInfo param);
    List<Map<String, Object>> selectMonthlyIncome(@Param("startDate") String startDate);

    List<Map<String, Object>> selectIncomeByType();

    Map<String, Object> selectPaidOverview();

    List<Map<String, Object>> selectTopProductIncome();
}