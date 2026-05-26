package com.catadoption.service;

import com.catadoption.entity.Adoption;
import com.catadoption.entity.Cat;
import com.catadoption.dao.AdoptionDao;
import com.catadoption.dao.CatDao;
import com.catadoption.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AdoptionService {
    
    @Autowired
    private AdoptionDao adoptionDao;
    
    @Autowired
    private CatDao catDao;
    
    @Autowired
    private UserDao userDao;
    
    public List<Adoption> getAllAdoptions() {
        return adoptionDao.findAll();
    }
    
    public List<Adoption> getAdoptionsByCatId(Long catId) {
        return adoptionDao.findByCatId(catId);
    }
    
    public List<Adoption> getAdoptionsByUserId(Long userId) {
        return adoptionDao.findByUserId(userId);
    }
    
    @Transactional
    public boolean applyForAdoption(Adoption adoption) {
        if (adoptionDao.insert(adoption) > 0) {
            return true;
        }
        return false;
    }
    
    @Transactional
    public boolean approveAdoption(Long id, boolean approved) {
        Adoption adoption = adoptionDao.findById(id);
        if (adoption == null) return false;
        
        adoption.setStatus(approved ? "approved" : "rejected");
        adoption.setReason(approved ? "已通过" : "不符合领养条件");
        
        boolean success = adoptionDao.update(adoption) > 0;
        
        if (success && approved) {
            Cat cat = catDao.findById(adoption.getCatId());
            if (cat != null) {
                cat.setStatus("adopted");
                catDao.update(cat);
            }
        }
        return success;
    }
}