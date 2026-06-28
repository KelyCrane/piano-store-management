package com.piano.management.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.piano.management.entity.OrderInfo;
import com.piano.management.mapper.OrderInfoMapper;
import com.piano.management.service.OrderInfoService;
import org.springframework.stereotype.Service;

@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {
}