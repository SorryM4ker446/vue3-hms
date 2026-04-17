package com.example.hms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.hms.entity.Bed;
import com.example.hms.entity.Patient;
import com.example.hms.mapper.BedMapper;
import com.example.hms.mapper.PatientMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired
    private PatientMapper patientMapper;
    @Autowired
    private BedMapper bedMapper;

    // 修改：获取所有病人 (支持搜索)
    @GetMapping
    public List<Patient> getAllPatients(@RequestParam(required = false) String keyword) {
        QueryWrapper<Patient> query = new QueryWrapper<>();

        // 如果前端传了 keyword，则模糊匹配 name 或 id_card
        if (keyword != null && !keyword.isEmpty()) {
            query.and(wrapper -> wrapper.like("name", keyword).or().like("id_card", keyword));
        }

        return patientMapper.selectList(query);
    }

    // 获取空闲床位
    @GetMapping("/beds/available")
    public List<Bed> getAvailableBeds() {
        return bedMapper.selectList(new QueryWrapper<Bed>().eq("status", 0));
    }

    // 办理入院 (Stored Procedure)
    @PostMapping("/admit")
    public Map<String, Object> admitPatient(@RequestBody Map<String, Object> req) {
        Map<String, Object> params = new HashMap<>();
        params.put("p_name", req.get("name"));
        params.put("p_gender", req.get("gender"));
        params.put("p_age", req.get("age"));
        params.put("p_id_card", req.get("idCard"));
        params.put("p_bed_id", req.get("bedId"));
        params.put("p_result", null);

        patientMapper.callAdmitPatient(params);

        Map<String, Object> res = new HashMap<>();
        String resultMsg = (String) params.get("p_result");
        res.put("message", resultMsg);
        // 如果存储过程返回的消息包含"成功"，则视为操作成功
        res.put("success", resultMsg != null && resultMsg.contains("成功"));
        return res;
    }

    // 办理出院 (修复版：强制更新 bed_id 为 NULL)
    @PostMapping("/discharge/{patientId}")
    public Map<String, Object> dischargePatient(@PathVariable Integer patientId) {
        UpdateWrapper<Patient> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("patient_id", patientId)
                .set("bed_id", null)  // 核心：强制设为 NULL 以触发触发器
                .set("status", 0);

        patientMapper.update(null, updateWrapper);

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "出院成功，床位已自动释放");
        return res;
    }

    // 修改病人信息
    @PutMapping
    public boolean updatePatient(@RequestBody Patient patient) {
        return patientMapper.updateById(patient) > 0;
    }

    // 删除病人
    @DeleteMapping("/{id}")
    public boolean deletePatient(@PathVariable Integer id) {
        return patientMapper.deleteById(id) > 0;
    }
}

