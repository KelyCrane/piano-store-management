package com.piano.management.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.piano.management.entity.Classroom;
import com.piano.management.mapper.ClassroomMapper;
import com.piano.management.service.ClassroomService;
import org.springframework.stereotype.Service;

@Service
public class ClassroomServiceImpl extends ServiceImpl<ClassroomMapper, Classroom> implements ClassroomService {
}