package com.piano.management.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.piano.management.entity.ClassRecord;
import com.piano.management.mapper.ClassRecordMapper;
import com.piano.management.service.ClassRecordService;
import org.springframework.stereotype.Service;

@Service
public class ClassRecordServiceImpl extends ServiceImpl<ClassRecordMapper, ClassRecord> implements ClassRecordService {
}