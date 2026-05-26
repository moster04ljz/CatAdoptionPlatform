package com.catadoption.controller;

import com.catadoption.common.Result;
import com.catadoption.entity.Carousel;
import com.catadoption.service.CarouselService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carousel")
public class CarouselController {

    @Autowired
    private CarouselService carouselService;

    @GetMapping("/active")
    public Result<List<Carousel>> getActive() {
        return Result.success(carouselService.getActiveCarousels());
    }

    @GetMapping("/list")
    public Result<List<Carousel>> list(HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"admin".equals(role)) {
            return Result.error(403, "无管理员权限");
        }
        return Result.success(carouselService.getAllCarousels());
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody Carousel carousel, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"admin".equals(role)) {
            return Result.error(403, "无管理员权限");
        }
        boolean success = carouselService.addCarousel(carousel);
        return success ? Result.success("添加成功", null) : Result.error("添加失败");
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody Carousel carousel, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"admin".equals(role)) {
            return Result.error(403, "无管理员权限");
        }
        boolean success = carouselService.updateCarousel(carousel);
        return success ? Result.success("更新成功", null) : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"admin".equals(role)) {
            return Result.error(403, "无管理员权限");
        }
        boolean success = carouselService.deleteCarousel(id);
        return success ? Result.success("删除成功", null) : Result.error("删除失败");
    }
}
