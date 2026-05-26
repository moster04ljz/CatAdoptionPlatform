package com.catadoption.controller;

import com.catadoption.common.Result;
import com.catadoption.entity.Adoption;
import com.catadoption.service.AdoptionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/adoption")
public class AdoptionController {

    @Autowired
    private AdoptionService adoptionService;

    @GetMapping("/list")
    public Result<List<Adoption>> list(HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"admin".equals(role)) {
            return Result.error(403, "无管理员权限");
        }
        return Result.success(adoptionService.getAllAdoptions());
    }

    @GetMapping("/cat/{catId}")
    public Result<List<Adoption>> getByCatId(@PathVariable Long catId) {
        return Result.success(adoptionService.getAdoptionsByCatId(catId));
    }

    @GetMapping("/user/{userId}")
    public Result<List<Adoption>> getByUserId(@PathVariable Long userId, HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        if (!currentUserId.equals(userId) && !"admin".equals(role)) {
            return Result.error(403, "无权查看");
        }
        return Result.success(adoptionService.getAdoptionsByUserId(userId));
    }

    @PostMapping("/apply")
    public Result<Void> apply(@RequestBody Adoption adoption, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        adoption.setUserId(userId);
        boolean success = adoptionService.applyForAdoption(adoption);
        return success ? Result.success("申请已提交", null) : Result.error("申请失败");
    }

    @PutMapping("/approve")
    public Result<Void> approve(@RequestBody java.util.Map<String, Object> params, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"admin".equals(role)) {
            return Result.error(403, "无管理员权限");
        }
        Long id = Long.valueOf(params.get("id").toString());
        boolean approved = Boolean.valueOf(params.get("approved").toString());
        boolean success = adoptionService.approveAdoption(id, approved);
        return success ? Result.success(approved ? "已批准" : "已拒绝", null) : Result.error("操作失败");
    }
}
