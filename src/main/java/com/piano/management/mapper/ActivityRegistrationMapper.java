package com.piano.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.piano.management.entity.ActivityRegistration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface ActivityRegistrationMapper extends BaseMapper<ActivityRegistration> {
    List<ActivityRegistration> selectPageWithNames(@Param("param") ActivityRegistration param);

    /** 各活动报名人数（图表用） */
    List<Map<String, Object>> selectParticipationByActivity();
}