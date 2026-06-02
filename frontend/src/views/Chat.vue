<template>
  <div class="chat">
    <el-page-header @back="$router.back()" content="私信" />

    <div class="chat-container">
      <!-- 会话列表 -->
      <div class="conversation-list">
        <div v-for="conv in conversations" :key="conv.id" class="conv-item"
             :class="{ active: currentChat && currentChat.id === conv.id }"
             @click="openChat(conv)">
          <el-avatar :size="40" :src="getOtherAvatar(conv)">{{ getOtherName(conv)?.[0] }}</el-avatar>
          <div class="conv-info">
            <div class="conv-header">
              <span class="conv-name">{{ getOtherName(conv) }}</span>
            </div>
            <div class="conv-last" v-if="conv.lastMessageContent">{{ conv.lastMessageContent }}</div>
            <div class="conv-last" v-else-if="conv.catName">关于：{{ conv.catName }}</div>
          </div>
        </div>
        <el-empty v-if="conversations.length === 0" description="暂无对话" :image-size="60" />
      </div>

      <!-- 聊天窗口 -->
      <div class="chat-window" v-if="currentChat">
        <div class="chat-header">
          <h3>{{ currentChat.otherName }}</h3>
          <span v-if="currentChat.catName" class="cat-tag">关于：{{ currentChat.catName }}</span>
        </div>
        <div class="chat-messages" ref="messagesRef">
          <div v-for="msg in messages" :key="msg.id" class="message-item" :class="{ mine: msg.senderId === user.id }">
            <el-avatar :size="32" :src="msg.senderId === user.id ? user.avatar : currentChat.otherAvatar">
              {{ msg.senderId === user.id ? user.nickname?.[0] : currentChat.otherName?.[0] }}
            </el-avatar>
            <div class="msg-bubble">
              <p>{{ msg.content }}</p>
              <span class="msg-time">{{ formatTime(msg.createTime) }}</span>
            </div>
          </div>
          <el-empty v-if="messages.length === 0" description="开始聊天吧" :image-size="60" />
        </div>
        <div class="chat-input">
          <el-input v-model="newMessage" placeholder="输入消息..." @keyup.enter="sendMsg" />
          <el-button type="primary" @click="sendMsg" :disabled="!newMessage.trim()">发送</el-button>
        </div>
      </div>
      <div class="chat-empty" v-else>
        <el-empty description="选择一个对话开始聊天" :image-size="100" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getConversations, getConversation, getChatMessages, sendMessage } from '../api'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const user = ref({})
const conversations = ref([])
const messages = ref([])
const newMessage = ref('')
const messagesRef = ref(null)
const currentChat = ref(null)

const loadUser = () => {
  try { user.value = JSON.parse(localStorage.getItem('user')) || {} } catch (e) { user.value = {} }
}

const getOtherId = (conv) => {
  return conv.user1Id === user.value.id ? conv.user2Id : conv.user1Id
}

const getOtherName = (conv) => {
  return conv.user1Id === user.value.id ? (conv.receiverName || '用户') : (conv.senderName || '用户')
}

const getOtherAvatar = (conv) => {
  return conv.user1Id === user.value.id ? conv.receiverAvatar : conv.senderAvatar
}

const loadConversations = async () => {
  try {
    const res = await getConversations()
    if (res.data.code === 200) {
      conversations.value = res.data.data || []
      // URL 带参数，自动打开对应对话或创建新会话
      if (route.query.otherId && !currentChat.value) {
        const targetId = Number(route.query.otherId)
        const conv = conversations.value.find(c => getOtherId(c) === targetId && (route.query.catId ? c.catId === Number(route.query.catId) : true))
        if (conv) {
          openChat(conv)
        } else {
          await createAndOpenChat(targetId, route.query.catId ? Number(route.query.catId) : null)
        }
      }
    }
  } catch (e) { console.error('loadConversations error', e) }
}

const createAndOpenChat = async (otherId, catId) => {
  try {
    const res = await getConversation(otherId, catId)
    if (res.data.code === 200 && res.data.data) {
      const conv = res.data.data
      // 补充到会话列表
      conversations.value.unshift(conv)
      openChat(conv)
    }
  } catch (e) {
    console.error('createAndOpenChat error', e)
  }
}

const openChat = async (conv) => {
  const otherId = getOtherId(conv)
  currentChat.value = {
    id: conv.id,
    otherId,
    otherName: getOtherName(conv),
    otherAvatar: getOtherAvatar(conv),
    catId: conv.catId,
    catName: conv.catName
  }
  await loadMessages(conv.id)
}

const loadMessages = async (conversationId) => {
  try {
    const res = await getChatMessages(conversationId)
    if (res.data.code === 200) {
      messages.value = res.data.data || []
      await nextTick()
      scrollToBottom()
    }
  } catch (e) { console.error('loadMessages error', e) }
}

const sendMsg = async () => {
  if (!newMessage.value.trim() || !currentChat.value) return
  try {
    const res = await sendMessage({
      receiverId: currentChat.value.otherId,
      catId: currentChat.value.catId,
      content: newMessage.value.trim()
    })
    if (res.data.code === 200) {
      newMessage.value = ''
      // 重新加载消息
      await loadMessages(currentChat.value.id)
      // 刷新会话列表（lastMessageContent 更新）
      const convRes = await getConversations()
      if (convRes.data.code === 200) conversations.value = convRes.data.data || []
      // 更新 currentChat 的引用
      const updated = conversations.value.find(c => c.id === currentChat.value.id)
      if (updated) {
        currentChat.value.otherName = getOtherName(updated)
        currentChat.value.otherAvatar = getOtherAvatar(updated)
        currentChat.value.catName = updated.catName
      }
    }
  } catch (e) {
    ElMessage.error('发送失败')
  }
}

const scrollToBottom = () => {
  if (messagesRef.value) {
    messagesRef.value.scrollTop = messagesRef.value.scrollHeight
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const d = new Date(time)
  const now = new Date()
  if (d.toDateString() === now.toDateString()) {
    return d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  }
  return d.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' }) + ' ' + d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

onMounted(() => {
  loadUser()
  loadConversations()
})
</script>

<style scoped>
.chat { max-width: 1200px; margin: 0 auto; padding: 20px; }
.chat-container { display: flex; gap: 16px; margin-top: 20px; height: calc(100vh - 160px); }
.conversation-list { width: 280px; border: 1px solid #e4e7ed; border-radius: 8px; overflow-y: auto; background: #fff; }
.conv-item { display: flex; align-items: center; gap: 12px; padding: 12px 16px; cursor: pointer; border-bottom: 1px solid #f0f0f0; transition: background 0.2s; }
.conv-item:hover { background: #f5f7fa; }
.conv-item.active { background: #ecf5ff; border-left: 3px solid #409eff; }
.conv-info { flex: 1; overflow: hidden; }
.conv-header { display: flex; justify-content: space-between; }
.conv-name { font-weight: 500; font-size: 14px; }
.conv-last { font-size: 12px; color: #999; margin-top: 4px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

.chat-window { flex: 1; display: flex; flex-direction: column; border: 1px solid #e4e7ed; border-radius: 8px; background: #fff; }
.chat-header { padding: 12px 20px; border-bottom: 1px solid #e4e7ed; display: flex; align-items: center; gap: 10px; }
.chat-header h3 { margin: 0; font-size: 16px; }
.cat-tag { font-size: 12px; color: #999; background: #f5f5f5; padding: 2px 8px; border-radius: 10px; }

.chat-messages { flex: 1; overflow-y: auto; padding: 20px; }
.message-item { display: flex; gap: 10px; margin-bottom: 16px; }
.message-item.mine { flex-direction: row-reverse; }
.msg-bubble { max-width: 60%; padding: 10px 14px; border-radius: 12px; background: #f4f4f4; position: relative; }
.message-item.mine .msg-bubble { background: #409eff; color: #fff; }
.msg-bubble p { margin: 0; word-break: break-word; }
.msg-time { display: block; font-size: 11px; color: #999; margin-top: 4px; text-align: right; }
.message-item.mine .msg-time { color: rgba(255,255,255,0.7); }

.chat-input { display: flex; gap: 10px; padding: 12px 20px; border-top: 1px solid #e4e7ed; }
.chat-input .el-input { flex: 1; }

.chat-empty { flex: 1; display: flex; align-items: center; justify-content: center; border: 1px solid #e4e7ed; border-radius: 8px; background: #fff; }
</style>
