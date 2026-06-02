package com.catadoption.dao;

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
            "WHERE (m.sender_id = #{userId1} AND m.receiver_id = #{userId2}) " +
            "   OR (m.sender_id = #{userId2} AND m.receiver_id = #{userId1}) " +
            "ORDER BY m.create_time ASC")
    List<Message> findConversation(Long userId1, Long userId2);

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

    @Select("SELECT MAX(m.id) AS id, m.sender_id, m.receiver_id, " +
            "u1.nickname AS sender_name, u1.avatar AS sender_avatar, " +
            "u2.nickname AS receiver_name, u2.avatar AS receiver_avatar, " +
            "c.name AS cat_name, c.image_url AS cat_image " +
            "FROM message m " +
            "LEFT JOIN user u1 ON m.sender_id = u1.id " +
            "LEFT JOIN user u2 ON m.receiver_id = u2.id " +
            "LEFT JOIN cat c ON m.cat_id = c.id " +
            "WHERE m.sender_id = #{userId} OR m.receiver_id = #{userId} " +
            "GROUP BY CASE WHEN m.sender_id < m.receiver_id " +
            "     THEN CONCAT(m.sender_id, '-', m.receiver_id) " +
            "     ELSE CONCAT(m.receiver_id, '-', m.sender_id) END " +
            "ORDER BY id DESC")
    List<Message> findConversations(Long userId);

    @Insert("INSERT INTO message(sender_id, receiver_id, cat_id, content, is_read, create_time) " +
            "VALUES(#{senderId}, #{receiverId}, #{catId}, #{content}, 0, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Message message);

    @Update("UPDATE message SET is_read = 1 WHERE receiver_id = #{receiverId} AND sender_id = #{senderId}")
    int markRead(@Param("receiverId") Long receiverId, @Param("senderId") Long senderId);
}
