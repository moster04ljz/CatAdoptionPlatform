package com.catadoption.service;

import com.catadoption.entity.Comment;
import com.catadoption.dao.CommentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CommentService {
    
    @Autowired
    private CommentDao commentDao;
    
    public List<Comment> getCommentsByCatId(Long catId) {
        return commentDao.findByCatId(catId);
    }
    
    public boolean addComment(Comment comment) {
        return commentDao.insert(comment) > 0;
    }
    
    public boolean deleteComment(Long id) {
        return commentDao.delete(id) > 0;
    }
}