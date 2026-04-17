package com.example.hms.controller;

import com.example.hms.entity.Bed;
import com.example.hms.mapper.BedMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/beds")
public class BedController {
    @Autowired private BedMapper bedMapper;

    @PostMapping
    public void add(@RequestBody Bed bed) {
        if(bed.getStatus() == null) bed.setStatus(0);
        bedMapper.insert(bed);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        bedMapper.deleteById(id);
    }
}