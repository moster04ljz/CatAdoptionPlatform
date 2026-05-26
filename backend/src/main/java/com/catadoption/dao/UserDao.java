package com.catadoption.dao;

import com.catadoption.entity.User;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserDao {
    
    @Select("SELECT * FROM user ORDER BY create_time DESC")
    List<User> findAll();
    
    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);
    
    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username);
    
    @Insert("INSERT INTO user(username, password, nickname, email, phone, role, create_time) " +
            "VALUES(#{username}, #{password}, #{nickname}, #{email}, #{phone}, 'user', NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
    
    @Update("<script>" +
            "UPDATE user " +
            "<set>" +
            "  <if test='nickname != null'>nickname=#{nickname},</if>" +
            "  <if test='email != null'>email=#{email},</if>" +
            "  <if test='phone != null'>phone=#{phone},</if>" +
            "  <if test='avatar != null'>avatar=#{avatar},</if>" +
            "  <if test='password != null and password != \"\"'>password=#{password},</if>" +
            "  update_time=NOW()" +
            "</set>" +
            " WHERE id=#{id}" +
            "</script>")
    int update(User user);

    @Update("UPDATE user SET password=#{password}, update_time=NOW() WHERE id=#{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);
}