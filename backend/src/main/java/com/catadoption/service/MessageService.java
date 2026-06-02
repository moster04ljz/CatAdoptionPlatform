package com.catadoption.service;

import com.catadoption.dao.MessageDao;
import com.catadoption.entity.Message;
import com.catadoption.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private UserService userService;

    public List<Message> getConversations(Long userId) {
        return messageDao.findConversations(userId);
    }

    public List<Message> getConversation(Long userId1, Long userId2) {
        return messageDao.findConversation(userId1, userId2);
    }

    public List<Message> getUnread(Long userId) {
        return messageDao.findUnreadByReceiver(userId);
    }

    public boolean canSendMessage(Long senderId, Long receiverId) {
        if (senderId == null || receiverId == null || senderId.equals(receiverId)) {
            return false;
        }

        User receiver = userService.getUserById(receiverId);
        return receiver != null;
    }

    public boolean sendMessage(Message message) {
        return messageDao.insert(message) > 0;
    }

    public void markAsRead(Long receiverId, Long senderId) {
        messageDao.markRead(receiverId, senderId);
    }
}

