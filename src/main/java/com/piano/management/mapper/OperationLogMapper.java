package com.piano.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.piano.management.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}