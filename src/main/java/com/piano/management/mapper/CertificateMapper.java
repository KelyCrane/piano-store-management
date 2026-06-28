package com.piano.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.piano.management.entity.Certificate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CertificateMapper extends BaseMapper<Certificate> {
    List<Certificate> selectPageWithNames(@Param("param") Certificate param);
}