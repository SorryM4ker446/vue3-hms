package com.example.hms.controller;

import com.example.hms.entity.SysUser;
import com.example.hms.mapper.SysUserMapper;
import com.example.hms.security.RequireRoles;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequireRoles({1})
public class SysUserController {
    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    public SysUserController(SysUserMapper sysUserMapper, PasswordEncoder passwordEncoder) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<SysUser> list() { return sysUserMapper.selectList(null); }

    @PostMapping
    public void add(@RequestBody SysUser u) {
        if (!StringUtils.hasText(u.getPassword())) {
            u.setPassword(passwordEncoder.encode("123456"));
        } else if (!isBcryptHash(u.getPassword())) {
            u.setPassword(passwordEncoder.encode(u.getPassword()));
        }
        sysUserMapper.insert(u);
    }

    @PutMapping
    public void update(@RequestBody SysUser u) {
        // 前端未传 password 时，保持原密码不变
        if (!StringUtils.hasText(u.getPassword())) {
            u.setPassword(null);
        } else if (!isBcryptHash(u.getPassword())) {
            u.setPassword(passwordEncoder.encode(u.getPassword()));
        }
        sysUserMapper.updateById(u);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) { sysUserMapper.deleteById(id); }

    private boolean isBcryptHash(String password) {
        return password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$");
    }
}
