package com.example.hms.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("Department")
public class Department {
    @TableId(type = IdType.AUTO)
    private Integer deptId;
    private String deptName;
    private String deptPhone;
}
