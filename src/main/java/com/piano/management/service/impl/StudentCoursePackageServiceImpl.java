package com.piano.management.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.piano.management.entity.StudentCoursePackage;
import com.piano.management.mapper.StudentCoursePackageMapper;
import com.piano.management.service.StudentCoursePackageService;
import org.springframework.stereotype.Service;

@Service
public class StudentCoursePackageServiceImpl extends ServiceImpl<StudentCoursePackageMapper, StudentCoursePackage> implements StudentCoursePackageService {
}
