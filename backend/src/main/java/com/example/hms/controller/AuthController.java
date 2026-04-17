package com.example.hms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.hms.entity.LoginRequest;
import com.example.hms.entity.SysUser;
import com.example.hms.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private SysUserMapper sysUserMapper;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest loginRequest) {
        Map<String, Object> result = new HashMap<>();
        QueryWrapper<SysUser> query = new QueryWrapper<>();
        query.eq("username", loginRequest.getUsername());
        SysUser user = sysUserMapper.selectOne(query);

        if (user != null && user.getPassword().equals(loginRequest.getPassword())) {
            result.put("success", true);
            result.put("message", "登录成功");
            result.put("user", user);
        } else {
            result.put("success", false);
            result.put("message", "用户名或密码错误");
        }
        return result;
    }
}
