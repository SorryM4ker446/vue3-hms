package com.example.hms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.hms.dto.UserProfileDto;
import com.example.hms.entity.LoginRequest;
import com.example.hms.entity.SysUser;
import com.example.hms.mapper.SysUserMapper;
import com.example.hms.security.JwtTokenService;
import io.jsonwebtoken.Claims;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthController(SysUserMapper sysUserMapper, PasswordEncoder passwordEncoder, JwtTokenService jwtTokenService) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest loginRequest) {
        Map<String, Object> result = new HashMap<>();

        QueryWrapper<SysUser> query = new QueryWrapper<>();
        query.eq("username", loginRequest.getUsername());
        SysUser user = sysUserMapper.selectOne(query);

        if (user == null || !passwordMatched(loginRequest.getPassword(), user.getPassword())) {
            result.put("success", false);
            result.put("message", "用户名或密码错误");
            return result;
        }

        // 兼容历史明文密码：首次登录成功后自动升级为 BCrypt
        if (!isBcryptHash(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(loginRequest.getPassword()));
            sysUserMapper.updateById(user);
        }

        String accessToken = jwtTokenService.generateAccessToken(user);
        String refreshToken = jwtTokenService.generateRefreshToken(user);

        result.put("success", true);
        result.put("message", "登录成功");
        result.put("tokenType", "Bearer");
        result.put("expiresIn", jwtTokenService.getAccessTokenExpireSeconds());
        result.put("accessToken", accessToken);
        result.put("refreshToken", refreshToken);
        result.put("user", toUserProfile(user));
        return result;
    }

    @PostMapping("/refresh")
    public Map<String, Object> refreshToken(
            @RequestHeader(value = "X-Refresh-Token", required = false) String refreshTokenHeader,
            @RequestBody(required = false) Map<String, String> body
    ) {
        Map<String, Object> result = new HashMap<>();
        String refreshToken = refreshTokenHeader;

        if (!StringUtils.hasText(refreshToken) && body != null) {
            refreshToken = body.get("refreshToken");
        }

        if (!StringUtils.hasText(refreshToken)) {
            result.put("success", false);
            result.put("message", "缺少刷新令牌");
            return result;
        }

        try {
            Claims claims = jwtTokenService.parseClaims(refreshToken);
            String tokenType = claims.get("type", String.class);
            if (!"refresh".equals(tokenType)) {
                result.put("success", false);
                result.put("message", "刷新令牌无效");
                return result;
            }

            Integer userId = Integer.parseInt(claims.getSubject());
            SysUser user = sysUserMapper.selectById(userId);
            if (user == null) {
                result.put("success", false);
                result.put("message", "用户不存在");
                return result;
            }

            result.put("success", true);
            result.put("message", "刷新成功");
            result.put("tokenType", "Bearer");
            result.put("expiresIn", jwtTokenService.getAccessTokenExpireSeconds());
            result.put("accessToken", jwtTokenService.generateAccessToken(user));
            result.put("refreshToken", jwtTokenService.generateRefreshToken(user));
            return result;
        } catch (Exception ex) {
            result.put("success", false);
            result.put("message", "刷新令牌已过期或非法");
            return result;
        }
    }

    private boolean passwordMatched(String inputPassword, String dbPassword) {
        if (!StringUtils.hasText(inputPassword) || !StringUtils.hasText(dbPassword)) {
            return false;
        }
        if (isBcryptHash(dbPassword)) {
            return passwordEncoder.matches(inputPassword, dbPassword);
        }
        return inputPassword.equals(dbPassword);
    }

    private boolean isBcryptHash(String password) {
        return StringUtils.hasText(password)
                && (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$"));
    }

    private UserProfileDto toUserProfile(SysUser user) {
        UserProfileDto dto = new UserProfileDto();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setRealName(user.getRealName());
        dto.setRole(user.getRole());
        dto.setDeptId(user.getDeptId());
        return dto;
    }
}
