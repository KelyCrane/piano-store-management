package com.piano.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.piano.management.entity.ClassRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ClassRecordMapper extends BaseMapper<ClassRecord> {
    List<ClassRecord> selectPageWithNames(@Param("param") ClassRecord param);
}