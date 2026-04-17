package com.example.hms.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.method.HandlerMethod;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    private final JwtTokenService jwtTokenService;

    public JwtAuthInterceptor(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            writeUnauthorized(response, "未登录或Token缺失");
            return false;
        }

        String token = authHeader.substring(7).trim();

        try {
            Claims claims = jwtTokenService.parseClaims(token);
            String tokenType = claims.get("type", String.class);
            if (!"access".equals(tokenType)) {
                writeUnauthorized(response, "Token类型非法");
                return false;
            }

            Integer authUserId = Integer.parseInt(claims.getSubject());
            Integer authUserRole = extractRole(claims);
            if (authUserRole == null) {
                writeUnauthorized(response, "Token角色信息缺失");
                return false;
            }

            request.setAttribute("authUserId", authUserId);
            request.setAttribute("authUserRole", authUserRole);

            if (!checkRolePermission(handler, authUserRole)) {
                writeForbidden(response, "权限不足");
                return false;
            }
            return true;
        } catch (Exception ex) {
            writeUnauthorized(response, "Token无效或已过期");
            return false;
        }
    }

    private Integer extractRole(Claims claims) {
        Object roleObj = claims.get("role");
        if (roleObj == null) {
            return null;
        }
        if (roleObj instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(roleObj.toString());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private boolean checkRolePermission(Object handler, Integer authUserRole) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RequireRoles requireRoles = handlerMethod.getMethodAnnotation(RequireRoles.class);
        if (requireRoles == null) {
            requireRoles = handlerMethod.getBeanType().getAnnotation(RequireRoles.class);
        }
        if (requireRoles == null) {
            return true;
        }

        return Arrays.stream(requireRoles.value()).anyMatch(role -> role == authUserRole);
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"success\":false,\"message\":\"" + message + "\"}");
    }

    private void writeForbidden(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"success\":false,\"message\":\"" + message + "\"}");
    }
}
