package com.piano.management.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.piano.management.entity.Certificate;
import com.piano.management.mapper.CertificateMapper;
import com.piano.management.service.CertificateService;
import org.springframework.stereotype.Service;

@Service
public class CertificateServiceImpl extends ServiceImpl<CertificateMapper, Certificate> implements CertificateService {
}