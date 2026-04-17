package com.example.hms.dto;

import lombok.Data;

@Data
public class UserProfileDto {
    private Integer userId;
    private String username;
    private String realName;
    private Integer role;
    private Integer deptId;
}
