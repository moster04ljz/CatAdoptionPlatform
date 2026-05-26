package com.catadoption.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 认证拦截器
 * 从请求头 Authorization: Bearer <token> 中提取并验证 Token
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS 预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        // 如果没有 Token，检查是否是公开的 GET 请求（猫咪详情等）
        if (token == null) {
            if (isPublicGetRequest(request)) {
                return true;
            }
        }

        if (token != null && jwtUtil.validateToken(token)) {
            request.setAttribute("userId", jwtUtil.getUserId(token));
            request.setAttribute("username", jwtUtil.getUsername(token));
            request.setAttribute("role", jwtUtil.getRole(token));
            return true;
        }

        // 如果是公开 GET 请求，即使没有 Token 也放行（但不设置用户信息）
        if (isPublicGetRequest(request)) {
            return true;
        }

        // 未认证：返回 401
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"message\":\"未登录或登录已过期\"}");
        return false;
    }

    /**
     * 判断是否是公开的 GET 请求
     * 猫咪详情: GET /api/cat/{数字}
     * 评论列表: GET /api/comment/cat/{数字}（已在 excludePathPatterns 中排除）
     */
    private boolean isPublicGetRequest(HttpServletRequest request) {
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            return false;
        }

        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty()) {
            path = path.substring(contextPath.length());
        }

        // GET /api/cat/{纯数字} → 猫咪详情页（公开）
        if (path.matches("/api/cat/\\d+")) {
            return true;
        }

        return false;
    }
}
