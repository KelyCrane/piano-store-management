package com.piano.management.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.piano.management.entity.ActivityRegistration;
import com.piano.management.mapper.ActivityRegistrationMapper;
import com.piano.management.service.ActivityRegistrationService;
import org.springframework.stereotype.Service;

@Service
public class ActivityRegistrationServiceImpl extends ServiceImpl<ActivityRegistrationMapper, ActivityRegistration> implements ActivityRegistrationService {
}