package com.example.hms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.hms.entity.Bed;
import com.example.hms.entity.Room;
import com.example.hms.mapper.BedMapper;
import com.example.hms.mapper.RoomMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    @Autowired private RoomMapper roomMapper;
    @Autowired private BedMapper bedMapper;

    @GetMapping
    public List<Room> list() { return roomMapper.selectList(null); }

    @PostMapping
    public void add(@RequestBody Room r) { roomMapper.insert(r); }

    @PutMapping
    public void update(@RequestBody Room r) { roomMapper.updateById(r); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) { roomMapper.deleteById(id); }

    // 获取某个房间的床位
    @GetMapping("/{roomId}/beds")
    public List<Bed> getBeds(@PathVariable Integer roomId) {
        return bedMapper.selectList(new QueryWrapper<Bed>().eq("room_id", roomId));
    }

    // 更新床位状态
    @PutMapping("/beds/{bedId}/status")
    public void updateBedStatus(@PathVariable Integer bedId, @RequestBody Map<String, Integer> req) {
        Bed b = new Bed();
        b.setBedId(bedId);
        b.setStatus(req.get("status"));
        bedMapper.updateById(b);
    }
}
