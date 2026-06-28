package com.piano.management.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.piano.management.entity.StockRecord;
import com.piano.management.mapper.StockRecordMapper;
import com.piano.management.service.StockRecordService;
import org.springframework.stereotype.Service;

@Service
public class StockRecordServiceImpl extends ServiceImpl<StockRecordMapper, StockRecord> implements StockRecordService {
}