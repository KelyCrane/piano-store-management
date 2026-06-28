package com.piano.management.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.piano.management.entity.TeacherQualification;
import com.piano.management.mapper.TeacherQualificationMapper;
import com.piano.management.service.TeacherQualificationService;
import org.springframework.stereotype.Service;

@Service
public class TeacherQualificationServiceImpl extends ServiceImpl<TeacherQualificationMapper, TeacherQualification> implements TeacherQualificationService {
}