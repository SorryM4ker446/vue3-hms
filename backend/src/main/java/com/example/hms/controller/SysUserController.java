package com.example.hms.controller;

import com.example.hms.entity.SysUser;
import com.example.hms.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class SysUserController {
    @Autowired private SysUserMapper sysUserMapper;

    @GetMapping
    public List<SysUser> list() { return sysUserMapper.selectList(null); }

    @PostMapping
    public void add(@RequestBody SysUser u) { sysUserMapper.insert(u); }

    @PutMapping
    public void update(@RequestBody SysUser u) { sysUserMapper.updateById(u); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) { sysUserMapper.deleteById(id); }
}
