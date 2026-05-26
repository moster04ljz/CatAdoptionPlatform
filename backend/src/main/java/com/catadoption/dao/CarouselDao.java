package com.catadoption.dao;

import com.catadoption.entity.Carousel;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CarouselDao {
    
    @Select("SELECT * FROM carousel WHERE status = 'active' ORDER BY sort_order ASC")
    List<Carousel> findActive();
    
    @Select("SELECT * FROM carousel ORDER BY sort_order ASC")
    List<Carousel> findAll();
    
    @Insert("INSERT INTO carousel(cat_id, image_url, title, link_url, sort_order, status) " +
            "VALUES(#{catId}, #{imageUrl}, #{title}, #{linkUrl}, #{sortOrder}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Carousel carousel);
    
    @Update("UPDATE carousel SET cat_id=#{catId}, image_url=#{imageUrl}, title=#{title}, " +
            "link_url=#{linkUrl}, sort_order=#{sortOrder}, status=#{status} WHERE id=#{id}")
    int update(Carousel carousel);
    
    @Delete("DELETE FROM carousel WHERE id = #{id}")
    int delete(Long id);
}