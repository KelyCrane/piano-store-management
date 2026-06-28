package com.piano.management.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.piano.management.entity.CourseBooking;
import com.piano.management.mapper.CourseBookingMapper;
import com.piano.management.service.CourseBookingService;
import org.springframework.stereotype.Service;

@Service
public class CourseBookingServiceImpl extends ServiceImpl<CourseBookingMapper, CourseBooking> implements CourseBookingService {
}