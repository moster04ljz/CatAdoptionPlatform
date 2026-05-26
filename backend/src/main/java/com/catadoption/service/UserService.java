package com.catadoption.service;

import com.catadoption.entity.User;
import com.catadoption.dao.UserDao;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserDao userDao;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * 登录验证（兼容旧明文密码，自动迁移）
     */
    public User login(String username, String password) {
        User user = userDao.findByUsername(username);
        if (user == null) return null;

        // 优先使用 BCrypt 验证
        if (passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }

        // 兼容旧明文密码：明文匹配成功后自动升级为 BCrypt
        if (password.equals(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(password));
            userDao.updatePassword(user.getId(), user.getPassword());
            return user;
        }

        return null;
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public User getUserById(Long id) {
        return userDao.findById(id);
    }

    /**
     * 注册（密码自动 BCrypt 加密）
     */
    public boolean register(User user) {
        if (userDao.findByUsername(user.getUsername()) != null) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userDao.insert(user) > 0;
    }

    public boolean updateUser(User user) {
        // 如果传入了新密码且不是 BCrypt 格式，则加密
        if (user.getPassword() != null && !user.getPassword().isEmpty()
                && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userDao.update(user) > 0;
    }
}
