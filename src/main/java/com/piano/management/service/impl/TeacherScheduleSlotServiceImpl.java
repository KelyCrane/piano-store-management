package com.piano.management.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.piano.management.entity.TeacherScheduleSlot;
import com.piano.management.mapper.TeacherScheduleSlotMapper;
import com.piano.management.service.TeacherScheduleSlotService;
import org.springframework.stereotype.Service;

@Service
public class TeacherScheduleSlotServiceImpl extends ServiceImpl<TeacherScheduleSlotMapper, TeacherScheduleSlot>
        implements TeacherScheduleSlotService {
}
