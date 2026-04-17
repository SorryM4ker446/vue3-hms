package com.example.hms.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.List;

@Data
@TableName("Room")
public class Room {
    @TableId(type = IdType.AUTO)
    private Integer roomId;
    private String roomNumber;
    private String type;
    private Integer deptId;

    // 这是一个非数据库字段，用于前端展示该房间下的床位
    @TableField(exist = false)
    private List<Bed> beds;
}
