package com.catadoption.service;

import com.catadoption.entity.Product;
import com.catadoption.dao.ProductDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {
    
    @Autowired
    private ProductDao productDao;
    
    public List<Product> getAllProducts() {
        return productDao.findAll();
    }
    
    public List<Product> getProductsByCategory(String category) {
        return productDao.findByCategory(category);
    }
    
    public Product getProductById(Long id) {
        return productDao.findById(id);
    }
    
    public boolean addProduct(Product product) {
        return productDao.insert(product) > 0;
    }
    
    public boolean updateProduct(Product product) {
        return productDao.update(product) > 0;
    }
    
    public boolean deleteProduct(Long id) {
        return productDao.delete(id) > 0;
    }
}