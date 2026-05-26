package com.catadoption.dao;

import com.catadoption.entity.Comment;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CommentDao {
    
    @Select("SELECT c.*, u.username, u.nickname, u.avatar FROM comment c " +
            "LEFT JOIN user u ON c.user_id = u.id " +
            "WHERE c.cat_id = #{catId} ORDER BY c.create_time DESC")
    List<Comment> findByCatId(Long catId);
    
    @Insert("INSERT INTO comment(cat_id, user_id, content, parent_id, create_time) " +
            "VALUES(#{catId}, #{userId}, #{content}, #{parentId}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Comment comment);
    
    @Delete("DELETE FROM comment WHERE id = #{id}")
    int delete(Long id);
}