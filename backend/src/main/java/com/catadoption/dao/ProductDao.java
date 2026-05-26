package com.catadoption.dao;

import com.catadoption.entity.Product;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ProductDao {
    
    @Select("SELECT * FROM product WHERE status = 'available' ORDER BY create_time DESC")
    List<Product> findAll();
    
    @Select("SELECT * FROM product WHERE category = #{category} AND status = 'available' ORDER BY create_time DESC")
    List<Product> findByCategory(String category);
    
    @Select("SELECT * FROM product WHERE id = #{id}")
    Product findById(Long id);
    
    @Insert("INSERT INTO product(name, category, brand, price, stock, image_url, description, status) " +
            "VALUES(#{name}, #{category}, #{brand}, #{price}, #{stock}, #{imageUrl}, #{description}, 'available')")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Product product);
    
    @Update("UPDATE product SET name=#{name}, category=#{category}, brand=#{brand}, price=#{price}, " +
            "stock=#{stock}, image_url=#{imageUrl}, description=#{description}, status=#{status} WHERE id=#{id}")
    int update(Product product);
    
    @Delete("DELETE FROM product WHERE id = #{id}")
    int delete(Long id);
}