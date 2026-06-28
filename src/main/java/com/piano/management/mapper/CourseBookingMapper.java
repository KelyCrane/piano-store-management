package com.piano.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.piano.management.entity.CourseBooking;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CourseBookingMapper extends BaseMapper<CourseBooking> {
    List<CourseBooking> selectPageWithNames(@Param("param") CourseBooking param);

    List<Map<String, Object>> selectBookingCountByStatus();
}