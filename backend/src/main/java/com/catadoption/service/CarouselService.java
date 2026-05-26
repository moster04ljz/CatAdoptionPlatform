package com.catadoption.service;

import com.catadoption.entity.Carousel;
import com.catadoption.dao.CarouselDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CarouselService {
    
    @Autowired
    private CarouselDao carouselDao;
    
    public List<Carousel> getActiveCarousels() {
        return carouselDao.findActive();
    }
    
    public List<Carousel> getAllCarousels() {
        return carouselDao.findAll();
    }
    
    public boolean addCarousel(Carousel carousel) {
        return carouselDao.insert(carousel) > 0;
    }
    
    public boolean updateCarousel(Carousel carousel) {
        return carouselDao.update(carousel) > 0;
    }
    
    public boolean deleteCarousel(Long id) {
        return carouselDao.delete(id) > 0;
    }
}