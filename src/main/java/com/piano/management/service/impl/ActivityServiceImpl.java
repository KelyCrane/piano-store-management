package com.piano.management.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.piano.management.entity.Activity;
import com.piano.management.mapper.ActivityMapper;
import com.piano.management.service.ActivityService;
import org.springframework.stereotype.Service;

@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {
}