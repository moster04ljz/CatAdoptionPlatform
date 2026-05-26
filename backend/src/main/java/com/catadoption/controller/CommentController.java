package com.catadoption.controller;

import com.catadoption.common.Result;
import com.catadoption.entity.Comment;
import com.catadoption.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/cat/{catId}")
    public Result<List<Comment>> getByCatId(@PathVariable Long catId) {
        return Result.success(commentService.getCommentsByCatId(catId));
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody Comment comment, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        comment.setUserId(userId);
        boolean success = commentService.addComment(comment);
        return success ? Result.success("评论成功", null) : Result.error("评论失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = commentService.deleteComment(id);
        return success ? Result.success("删除成功", null) : Result.error("删除失败");
    }
}
