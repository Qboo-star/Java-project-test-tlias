package com.kk.mapper;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

/**
 * 数据统计 Mapper
 */
@Mapper
public interface ReportMapper {

    // 按性别统计员工人数，返回字段 gender, cnt
    List<Map<String, Object>> empGenderData();

    // 按职位统计员工人数，返回字段 job, cnt
    List<Map<String, Object>> empJobData();

    // 按学历统计学员人数，返回字段 degree, cnt
    List<Map<String, Object>> studentDegreeData();

    // 按班级统计学员人数，返回字段 clazzName, cnt
    List<Map<String, Object>> studentCountData();
}
