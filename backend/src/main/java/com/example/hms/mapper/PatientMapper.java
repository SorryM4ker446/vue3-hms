package com.example.hms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hms.entity.Patient;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.StatementType;

import java.util.List;
import java.util.Map;

@Mapper
public interface PatientMapper extends BaseMapper<Patient> {

    // 调用存储过程 (保持不变)
    @Select("CALL proc_admit_patient(" +
            "#{p_name, mode=IN, jdbcType=VARCHAR}, " +
            "#{p_gender, mode=IN, jdbcType=CHAR}, " +
            "#{p_age, mode=IN, jdbcType=INTEGER}, " +
            "#{p_id_card, mode=IN, jdbcType=VARCHAR}, " +
            "#{p_bed_id, mode=IN, jdbcType=INTEGER}, " +
            "#{p_result, mode=OUT, jdbcType=VARCHAR})")
    @Options(statementType = StatementType.CALLABLE)
    void callAdmitPatient(Map<String, Object> params);

    // 修改：支持带关键字搜索的视图查询
    // 使用 <script> 标签实现动态 SQL：如果 keyword 不为空，则匹配 姓名 或 身份证
    @Select("<script>" +
            "SELECT * FROM vw_patient_bed_info " +
            "<where>" +
            "  <if test='keyword != null and keyword != \"\"'>" +
            "    (patient_name LIKE CONCAT('%',#{keyword},'%') OR id_card LIKE CONCAT('%',#{keyword},'%'))" +
            "  </if>" +
            "</where>" +
            "</script>")
    List<Map<String, Object>> selectDashboardView(@Param("keyword") String keyword);
}

