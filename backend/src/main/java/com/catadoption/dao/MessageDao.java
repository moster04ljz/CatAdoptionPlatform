package com.catadoption.dao;

import com.catadoption.entity.Conversation;
import com.catadoption.entity.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessageDao {

    @Select("SELECT m.*, u1.nickname AS sender_name, u1.avatar AS sender_avatar, " +
            "u2.nickname AS receiver_name, u2.avatar AS receiver_avatar, " +
            "c.name AS cat_name, c.image_url AS cat_image " +
            "FROM message m " +
            "LEFT JOIN user u1 ON m.sender_id = u1.id " +
            "LEFT JOIN user u2 ON m.receiver_id = u2.id " +
            "LEFT JOIN cat c ON m.cat_id = c.id " +
            "WHERE m.id = #{id}")
    Message findById(Long id);

    @Select("SELECT m.*, u1.nickname AS sender_name, u1.avatar AS sender_avatar, " +
            "u2.nickname AS receiver_name, u2.avatar AS receiver_avatar, " +
            "c.name AS cat_name, c.image_url AS cat_image " +
            "FROM message m " +
            "LEFT JOIN user u1 ON m.sender_id = u1.id " +
            "LEFT JOIN user u2 ON m.receiver_id = u2.id " +
            "LEFT JOIN cat c ON m.cat_id = c.id " +
            "WHERE m.conversation_id = #{conversationId} " +
            "ORDER BY m.create_time ASC")
    List<Message> findByConversationId(Long conversationId);

    @Select("SELECT c.id, c.user1_id AS user1Id, c.user2_id AS user2Id, c.cat_id AS catId, " +
            "c.last_message_content AS lastMessageContent, c.last_message_time AS lastMessageTime, " +
            "c.create_time AS createTime, c.update_time AS updateTime, " +
            "u1.nickname AS sender_name, u1.avatar AS sender_avatar, " +
            "u2.nickname AS receiver_name, u2.avatar AS receiver_avatar, " +
            "cat.name AS cat_name, cat.image_url AS cat_image " +
            "FROM conversation c " +
            "LEFT JOIN user u1 ON c.user1_id = u1.id " +
            "LEFT JOIN user u2 ON c.user2_id = u2.id " +
            "LEFT JOIN cat cat ON c.cat_id = cat.id " +
            "WHERE c.user1_id = #{userId} OR c.user2_id = #{userId} " +
            "ORDER BY c.last_message_time DESC")
    List<Conversation> findConversations(Long userId);

    @Select("SELECT c.id, c.user1_id AS user1Id, c.user2_id AS user2Id, c.cat_id AS catId, " +
            "c.last_message_content AS lastMessageContent, c.last_message_time AS lastMessageTime, " +
            "c.create_time AS createTime, c.update_time AS updateTime, " +
            "u1.nickname AS sender_name, u1.avatar AS sender_avatar, " +
            "u2.nickname AS receiver_name, u2.avatar AS receiver_avatar, " +
            "cat.name AS cat_name, cat.image_url AS cat_image " +
            "FROM conversation c " +
            "LEFT JOIN user u1 ON c.user1_id = u1.id " +
            "LEFT JOIN user u2 ON c.user2_id = u2.id " +
            "LEFT JOIN cat cat ON c.cat_id = cat.id " +
            "WHERE ((c.user1_id = #{userId1} AND c.user2_id = #{userId2}) " +
            "   OR (c.user1_id = #{userId2} AND c.user2_id = #{userId1})) " +
            "AND (c.cat_id <=> #{catId}) " +
            "LIMIT 1")
    Conversation findConversation(@Param("userId1") Long userId1,
                                  @Param("userId2") Long userId2,
                                  @Param("catId") Long catId);

    @Select("SELECT m.*, u1.nickname AS sender_name, u1.avatar AS sender_avatar, " +
            "u2.nickname AS receiver_name, u2.avatar AS receiver_avatar, " +
            "c.name AS cat_name, c.image_url AS cat_image " +
            "FROM message m " +
            "LEFT JOIN user u1 ON m.sender_id = u1.id " +
            "LEFT JOIN user u2 ON m.receiver_id = u2.id " +
            "LEFT JOIN cat c ON m.cat_id = c.id " +
            "WHERE m.receiver_id = #{userId} AND m.is_read = 0 " +
            "ORDER BY m.create_time DESC")
    List<Message> findUnreadByReceiver(Long userId);

    @Insert("INSERT INTO conversation(user1_id, user2_id, cat_id, last_message_time, create_time) " +
            "VALUES(#{user1Id}, #{user2Id}, #{catId}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertConversation(@Param("user1Id") Long user1Id,
                           @Param("user2Id") Long user2Id,
                           @Param("catId") Long catId);

    @Update("UPDATE conversation SET last_message_content = #{content}, last_message_time = NOW(), update_time = NOW() WHERE id = #{conversationId}")
    int updateConversationLastMessage(@Param("conversationId") Long conversationId,
                                       @Param("content") String content);

    @Insert("INSERT INTO message(conversation_id, sender_id, receiver_id, cat_id, content, is_read, create_time) " +
            "VALUES(#{conversationId}, #{senderId}, #{receiverId}, #{catId}, #{content}, 0, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Message message);

    @Update("UPDATE message SET is_read = 1 WHERE receiver_id = #{receiverId} AND sender_id = #{senderId}")
    int markRead(@Param("receiverId") Long receiverId,
                 @Param("senderId") Long senderId);
}
