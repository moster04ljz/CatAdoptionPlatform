package com.catadoption.controller;

import com.catadoption.common.Result;
import com.catadoption.entity.Product;
import com.catadoption.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/list")
    public Result<List<Product>> list(@RequestParam(required = false) String category) {
        List<Product> products = category != null
                ? productService.getProductsByCategory(category)
                : productService.getAllProducts();
        return Result.success(products);
    }

    @GetMapping("/{id}")
    public Result<Product> getById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return product != null ? Result.success(product) : Result.error(404, "商品不存在");
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody Product product, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"admin".equals(role)) {
            return Result.error(403, "无管理员权限");
        }
        boolean success = productService.addProduct(product);
        return success ? Result.success("添加成功", null) : Result.error("添加失败");
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody Product product, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"admin".equals(role)) {
            return Result.error(403, "无管理员权限");
        }
        boolean success = productService.updateProduct(product);
        return success ? Result.success("更新成功", null) : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"admin".equals(role)) {
            return Result.error(403, "无管理员权限");
        }
        boolean success = productService.deleteProduct(id);
        return success ? Result.success("删除成功", null) : Result.error("删除失败");
    }
}
