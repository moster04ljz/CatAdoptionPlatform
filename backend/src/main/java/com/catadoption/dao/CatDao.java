package com.catadoption.dao;

import com.catadoption.entity.Cat;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CatDao {

    @Select("<script>" +
            "SELECT c.*, u.nickname AS add_user_name, u.avatar AS add_user_avatar, u.role AS add_user_role FROM cat c " +
            "LEFT JOIN user u ON c.add_user_id = u.id " +
            "<where>" +
            "<if test='status != null'>AND c.status = #{status}</if>" +
            "<if test='category != null'>AND c.category = #{category}</if>" +
            "<if test='isHot != null and isHot == true'>AND c.is_hot = 1</if>" +
            "<if test='search != null and search != \"\"'>AND (c.name LIKE CONCAT('%', #{search}, '%') OR c.breed LIKE CONCAT('%', #{search}, '%'))</if>" +
            "</where>" +
            "ORDER BY c.create_time DESC" +
            "</script>")
    List<Cat> findByCondition(@Param("status") String status, @Param("category") String category,
                              @Param("isHot") Boolean isHot, @Param("search") String search);

    @Select("<script>" +
            "SELECT c.*, u.nickname AS add_user_name, u.avatar AS add_user_avatar, u.role AS add_user_role FROM cat c " +
            "LEFT JOIN user u ON c.add_user_id = u.id " +
            "<where>" +
            "<if test='status != null'>AND c.status = #{status}</if>" +
            "<if test='category != null'>AND c.category = #{category}</if>" +
            "<if test='isHot != null and isHot == true'>AND c.is_hot = 1</if>" +
            "<if test='search != null and search != \"\"'>AND (c.name LIKE CONCAT('%', #{search}, '%') OR c.breed LIKE CONCAT('%', #{search}, '%'))</if>" +
            "</where>" +
            "ORDER BY c.create_time DESC " +
            "LIMIT #{size} OFFSET #{offset}" +
            "</script>")
    List<Cat> findByConditionWithPage(@Param("status") String status, @Param("category") String category,
                                      @Param("isHot") Boolean isHot, @Param("search") String search,
                                      @Param("offset") int offset, @Param("size") int size);

    @Select("SELECT c.*, u.nickname AS add_user_name, u.avatar AS add_user_avatar, u.role AS add_user_role FROM cat c " +
            "LEFT JOIN user u ON c.add_user_id = u.id WHERE c.id = #{id}")
    Cat findById(Long id);

    @Select("SELECT c.*, u.nickname AS add_user_name, u.avatar AS add_user_avatar, u.role AS add_user_role FROM cat c " +
            "LEFT JOIN user u ON c.add_user_id = u.id WHERE c.add_user_id = #{addUserId} ORDER BY c.create_time DESC")
    List<Cat> findByAddUserId(Long addUserId);

    @Select("SELECT c.*, u.nickname AS add_user_name, u.avatar AS add_user_avatar, u.role AS add_user_role FROM cat c " +
            "LEFT JOIN user u ON c.add_user_id = u.id ORDER BY c.create_time DESC")
    List<Cat> findAll();

    @Select("SELECT * FROM cat WHERE status = 'pending_review' ORDER BY create_time DESC")
    List<Cat> findPendingReview();

    @Insert("INSERT INTO cat(name, breed, age, gender, color, description, image_url, status, category, add_user_id, is_hot, health_status, vaccine_record, is_sterilized, price, location, create_time) " +
            "VALUES(#{name}, #{breed}, #{age}, #{gender}, #{color}, #{description}, #{imageUrl}, #{status}, #{category}, #{addUserId}, #{isHot}, #{healthStatus}, #{vaccineRecord}, #{isSterilized}, #{price}, #{location}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Cat cat);

    @Update("UPDATE cat SET name=#{name}, breed=#{breed}, age=#{age}, gender=#{gender}, color=#{color}, description=#{description}, image_url=#{imageUrl}, status=#{status}, category=#{category}, is_hot=#{isHot}, health_status=#{healthStatus}, vaccine_record=#{vaccineRecord}, is_sterilized=#{isSterilized}, price=#{price}, location=#{location}, update_time=NOW() WHERE id=#{id}")
    int update(Cat cat);

    @Delete("DELETE FROM cat WHERE id = #{id}")
    int delete(Long id);

    @Delete("DELETE FROM comment WHERE cat_id = #{catId}")
    int deleteCommentsByCatId(Long catId);

    @Delete("DELETE FROM adoption WHERE cat_id = #{catId}")
    int deleteAdoptionsByCatId(Long catId);
}
