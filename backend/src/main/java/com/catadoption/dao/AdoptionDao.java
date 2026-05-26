package com.catadoption.dao;

import com.catadoption.entity.Adoption;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface AdoptionDao {
    
    @Select("SELECT a.*, c.name AS cat_name, u.nickname AS user_name FROM adoption a " +
            "LEFT JOIN cat c ON a.cat_id = c.id LEFT JOIN user u ON a.user_id = u.id " +
            "ORDER BY a.apply_time DESC")
    List<Adoption> findAll();
    
    @Select("SELECT a.*, c.name AS cat_name FROM adoption a " +
            "LEFT JOIN cat c ON a.cat_id = c.id WHERE a.cat_id = #{catId} ORDER BY a.apply_time DESC")
    List<Adoption> findByCatId(Long catId);
    
    @Select("SELECT a.*, c.name AS cat_name FROM adoption a " +
            "LEFT JOIN cat c ON a.cat_id = c.id WHERE a.user_id = #{userId} ORDER BY a.apply_time DESC")
    List<Adoption> findByUserId(Long userId);
    
    @Select("SELECT * FROM adoption WHERE id = #{id}")
    Adoption findById(Long id);
    
    @Insert("INSERT INTO adoption(cat_id, user_id, real_name, phone, occupation, housing, experience, reason, status, apply_time) " +
            "VALUES(#{catId}, #{userId}, #{realName}, #{phone}, #{occupation}, #{housing}, #{experience}, #{reason}, 'pending', NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Adoption adoption);
    
    @Update("UPDATE adoption SET status=#{status}, reason=#{reason}, update_time=NOW() WHERE id=#{id}")
    int update(Adoption adoption);
}