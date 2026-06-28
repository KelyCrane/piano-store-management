package com.piano.management.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.piano.management.entity.Banner;
import com.piano.management.mapper.BannerMapper;
import com.piano.management.service.BannerService;
import org.springframework.stereotype.Service;

@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {
}