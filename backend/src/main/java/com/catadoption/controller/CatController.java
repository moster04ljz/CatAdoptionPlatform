package com.catadoption.controller;

import com.catadoption.common.Result;
import com.catadoption.entity.Cat;
import com.catadoption.service.CatService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cat")
public class CatController {

    @Autowired
    private CatService catService;

    // ========== 公开接口（无需认证） ==========

    @GetMapping("/adoption")
    public Result<List<Cat>> getAdoptionList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(catService.getAllAdoptionCats(page, size));
    }

    @GetMapping("/market")
    public Result<List<Cat>> getMarketList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(catService.getAllMarketCats(page, size));
    }

    @GetMapping("/hot")
    public Result<List<Cat>> getHotList() {
        return Result.success(catService.getHotCats());
    }

    @GetMapping("/list")
    public Result<List<Cat>> list(@RequestParam(required = false) String category,
                                  @RequestParam(required = false) String search) {
        return Result.success(catService.getCatsByCondition("available", category, null, search));
    }

    @GetMapping("/{id}")
    public Result<Cat> getById(@PathVariable Long id) {
        Cat cat = catService.getCatById(id);
        return cat != null ? Result.success(cat) : Result.error(404, "猫咪不存在");
    }

    // ========== 需要认证的接口 ==========

    /**
     * 管理员获取所有猫咪（含所有状态）
     */
    @GetMapping("/admin/all")
    public Result<List<Cat>> adminList(HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"admin".equals(role)) {
            return Result.error(403, "无管理员权限");
        }
        return Result.success(catService.getAllCats());
    }

    @GetMapping("/my")
    public Result<List<Cat>> getMyCats(@RequestParam Long userId, HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        if (!currentUserId.equals(userId) && !"admin".equals(role)) {
            return Result.error(403, "无权查看");
        }
        return Result.success(catService.getCatsByAddUserId(userId));
    }

    @GetMapping("/pending")
    public Result<List<Cat>> getPending(HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"admin".equals(role)) {
            return Result.error(403, "无管理员权限");
        }
        return Result.success(catService.getPendingReviewCats());
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody Cat cat, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        cat.setAddUserId(userId);

        boolean success = catService.addCat(cat, role);
        return success ? Result.success("提交成功", null) : Result.error("添加失败");
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody Cat cat, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        boolean success = catService.updateCat(cat, userId, role);
        return success ? Result.success("更新成功", null) : Result.error(403, "无权操作");
    }

    @PutMapping("/approve")
    public Result<Void> approve(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"admin".equals(role)) {
            return Result.error(403, "无管理员权限");
        }
        Long id = Long.valueOf(params.get("id").toString());
        boolean approved = Boolean.valueOf(params.get("approved").toString());
        boolean success = catService.approveCat(id, approved);
        return success ? Result.success(approved ? "已通过审核" : "已拒绝", null) : Result.error("操作失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        boolean success = catService.deleteCat(id, userId, role);
        return success ? Result.success("删除成功", null) : Result.error(403, "无权操作");
    }
}
