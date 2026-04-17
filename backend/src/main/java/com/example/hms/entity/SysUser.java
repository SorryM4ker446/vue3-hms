package com.example.hms.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("SysUser")
public class SysUser {
    @TableId(type = IdType.AUTO)
    private Integer userId;
    private String username;
    private String password;
    private String realName;
    private Integer role; // 1=管理员, 2=医生, 3=护士
    private Integer deptId;
}
