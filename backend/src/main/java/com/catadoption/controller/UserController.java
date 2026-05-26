package com.catadoption.controller;

import com.catadoption.common.JwtUtil;
import com.catadoption.common.Result;
import com.catadoption.entity.User;
import com.catadoption.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody User loginUser) {
        User user = userService.login(loginUser.getUsername(), loginUser.getPassword());
        if (user != null) {
            // 生成 JWT Token
            String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
            user.setPassword(null);

            Map<String, Object> data = new HashMap<>();
            data.put("user", user);
            data.put("token", token);
            return Result.success("登录成功", data);
        }
        return Result.error(401, "用户名或密码错误");
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody User user) {
        boolean success = userService.register(user);
        return success ? Result.success("注册成功", null) : Result.error("用户名已存在");
    }

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user != null) user.setPassword(null);
        return user != null ? Result.success(user) : Result.error(404, "用户不存在");
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody User user) {
        boolean success = userService.updateUser(user);
        return success ? Result.success("更新成功", null) : Result.error("更新失败");
    }

    /**
     * 获取当前登录用户信息（从 Token 中解析）
     */
    @GetMapping("/current")
    public Result<User> getCurrentUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getUserById(userId);
        if (user != null) user.setPassword(null);
        return Result.success(user);
    }
}
