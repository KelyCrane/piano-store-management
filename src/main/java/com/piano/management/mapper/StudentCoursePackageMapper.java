package com.piano.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.piano.management.entity.StudentCoursePackage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentCoursePackageMapper extends BaseMapper<StudentCoursePackage> {
    List<StudentCoursePackage> selectPageWithNames(@Param("param") StudentCoursePackage param);
}
