package com.catadoption.controller;

import com.catadoption.common.Result;
import com.catadoption.entity.Message;
import com.catadoption.service.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/conversations")
    public Result<List<Message>> conversations(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(messageService.getConversations(userId));
    }

    @GetMapping("/chat/{otherUserId}")
    public Result<List<Message>> chat(@PathVariable Long otherUserId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        messageService.markAsRead(userId, otherUserId);
        return Result.success(messageService.getConversation(userId, otherUserId));
    }

    @GetMapping("/unread")
    public Result<List<Message>> unread(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(messageService.getUnread(userId));
    }

    @PostMapping("/send")
    public Result<Void> send(@RequestBody Message message, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (message.getReceiverId() == null || userId.equals(message.getReceiverId())) {
            return Result.error(400, "不能给自己发送私信");
        }

        if (!messageService.canSendMessage(userId, message.getReceiverId())) {
            return Result.error(400, "私信接收用户不存在");
        }

        message.setSenderId(userId);
        boolean success = messageService.sendMessage(message);
        return success ? Result.success("发送成功", null) : Result.error("发送失败");
    }
}
