package com.example.hms.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("Bed")
public class Bed {
    @TableId(type = IdType.AUTO)
    private Integer bedId;
    private String bedNumber;
    private Integer status; // 0=空闲, 1=占用, 2=维修
    private Integer roomId;
}
