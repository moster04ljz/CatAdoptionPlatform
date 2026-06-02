package com.catadoption.service;

import com.catadoption.entity.Cat;
import com.catadoption.dao.CatDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatService {

    @Autowired
    private CatDao catDao;

    public List<Cat> getCatsByCondition(String status, String category, Boolean isHot, String search) {
        return catDao.findByCondition(status, category, isHot, search);
    }

    public List<Cat> getAllAdoptionCats(Integer page, Integer size) {
        int offset = (page - 1) * size;
        return catDao.findByConditionWithPage("available", "adoption", null, null, offset, size);
    }

    public List<Cat> getAllMarketCats(Integer page, Integer size) {
        int offset = (page - 1) * size;
        return catDao.findByConditionWithPage("available", "market", null, null, offset, size);
    }

    public List<Cat> getHotCats() {
        return catDao.findByCondition("available", null, true, null);
    }

    public Cat getCatById(Long id) {
        return catDao.findById(id);
    }

    public List<Cat> getCatsByAddUserId(Long userId) {
        return catDao.findByAddUserId(userId);
    }

    public List<Cat> getPendingReviewCats() {
        return catDao.findByCondition("pending_review", null, null, null);
    }

    public List<Cat> getAllCats() {
        return catDao.findAll();
    }

    /**
     * 添加猫咪
     * @param role 当前用户角色
     */
    public boolean addCat(Cat cat, String role) {
        if ("admin".equals(role)) {
            cat.setStatus("available");
        } else {
            cat.setStatus("pending_review");
        }
        cat.setCategory("adoption");
        return catDao.insert(cat) > 0;
    }

    /**
     * 更新猫咪（带权限校验）
     */
    public boolean updateCat(Cat cat, Long currentUserId, String role) {
        Cat existing = catDao.findById(cat.getId());
        if (existing == null) return false;
        if (!"admin".equals(role) && !existing.getAddUserId().equals(currentUserId)) {
            return false;
        }
        return catDao.update(cat) > 0;
    }

    public boolean approveCat(Long id, boolean approved) {
        Cat cat = catDao.findById(id);
        if (cat == null) return false;
        cat.setStatus(approved ? "available" : "rejected");
        return catDao.update(cat) > 0;
    }

    /**
     * 删除猫咪（带权限校验，级联删除关联评论和领养申请）
     */
    public boolean deleteCat(Long id, Long currentUserId, String role) {
        Cat cat = catDao.findById(id);
        if (cat == null) return false;
        if (!"admin".equals(role) && !cat.getAddUserId().equals(currentUserId)) {
            return false;
        }
        catDao.deleteCommentsByCatId(id);
        catDao.deleteAdoptionsByCatId(id);
        return catDao.delete(id) > 0;
    }
}
