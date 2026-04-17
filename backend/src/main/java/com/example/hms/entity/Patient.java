package com.example.hms.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("Patient")
public class Patient {
    @TableId(type = IdType.AUTO)
    private Integer patientId;
    private String name;
    private String gender;
    private Integer age;
    private String idCard;
    private Integer status; // 1=在院, 0=出院
    private Integer bedId;
}
