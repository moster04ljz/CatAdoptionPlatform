package com.catadoption.controller;

import com.catadoption.common.Result;
import com.catadoption.entity.Conversation;
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
    public Result<List<Conversation>> conversations(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(messageService.getConversations(userId));
    }

    @GetMapping("/conversation")
    public Result<Conversation> conversation(@RequestParam Long otherUserId,
                                             @RequestParam(required = false) Long catId,
                                             HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Conversation conversation = messageService.createOrGetConversation(userId, otherUserId, catId);
        return conversation != null ? Result.success(conversation) : Result.error(404, "会话不存在");
    }

    @GetMapping("/{conversationId}")
    public Result<List<Message>> chat(@PathVariable Long conversationId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Message> messages = messageService.getMessages(conversationId);
        messageService.markConversationRead(conversationId, userId);
        return Result.success(messages);
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
        Conversation conversation = messageService.createOrGetConversation(userId, message.getReceiverId(), message.getCatId());
        if (conversation == null) {
            return Result.error(500, "创建会话失败");
        }
        message.setSenderId(userId);
        message.setConversationId(conversation.getId());
        boolean success = messageService.sendMessage(message);
        return success ? Result.success("发送成功", null) : Result.error("发送失败");
    }
}
