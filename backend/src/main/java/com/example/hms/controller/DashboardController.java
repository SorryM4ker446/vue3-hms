package com.example.hms.controller;

import com.example.hms.mapper.PatientMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private PatientMapper patientMapper;

    // 修改：增加 keyword 参数 (非必填)
    @GetMapping
    public List<Map<String, Object>> getDashboardView(@RequestParam(required = false) String keyword) {
        return patientMapper.selectDashboardView(keyword);
    }
}

