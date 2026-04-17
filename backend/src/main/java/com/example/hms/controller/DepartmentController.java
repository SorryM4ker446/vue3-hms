package com.example.hms.controller;

import com.example.hms.entity.Department;
import com.example.hms.mapper.DepartmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {
    @Autowired private DepartmentMapper departmentMapper;

    @GetMapping
    public List<Department> list() { return departmentMapper.selectList(null); }

    @PostMapping
    public void add(@RequestBody Department d) { departmentMapper.insert(d); }

    @PutMapping
    public void update(@RequestBody Department d) { departmentMapper.updateById(d); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) { departmentMapper.deleteById(id); }
}
