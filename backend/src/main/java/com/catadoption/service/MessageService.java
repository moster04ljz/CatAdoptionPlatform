package com.catadoption.service;

import com.catadoption.dao.MessageDao;
import com.catadoption.entity.Conversation;
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

    public List<Conversation> getConversations(Long userId) {
        return messageDao.findConversations(userId);
    }

    public Conversation createOrGetConversation(Long userId1, Long userId2, Long catId) {
        Conversation conversation = messageDao.findConversation(userId1, userId2, catId);
        if (conversation != null) {
            return conversation;
        }

        Long firstUserId = Math.min(userId1, userId2);
        Long secondUserId = Math.max(userId1, userId2);
        messageDao.insertConversation(firstUserId, secondUserId, catId);
        return messageDao.findConversation(firstUserId, secondUserId, catId);
    }

    public List<Message> getMessages(Long conversationId) {
        return messageDao.findByConversationId(conversationId);
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
        int inserted = messageDao.insert(message);
        if (inserted > 0) {
            messageDao.updateConversationLastMessage(message.getConversationId(), message.getContent());
            return true;
        }
        return false;
    }

    public void markConversationRead(Long conversationId, Long userId) {
        List<Message> messages = messageDao.findByConversationId(conversationId);
        for (Message message : messages) {
            if (!userId.equals(message.getSenderId())) {
                messageDao.markRead(message.getReceiverId(), message.getSenderId());
            }
        }
    }
}

