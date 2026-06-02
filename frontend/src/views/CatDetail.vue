<template>
  <div class="cat-detail">
    <div class="navbar">
      <div class="logo" @click="$router.push('/')">🐱 返回首页</div>
    </div>

    <div class="container" v-if="cat">
      <el-row :gutter="30">
        <el-col :span="12">
          <el-card class="img-card">
            <img :src="cat.imageUrl || defaultImage" class="main-img" />
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="info-card">
            <div class="header">
              <h1>{{ cat.name }}</h1>
              <el-tag type="success" size="large">免费领养</el-tag>
            </div>

            <div class="publisher-info" v-if="cat.addUserName">
              <el-avatar :size="40" :src="cat.addUserAvatar">{{ cat.addUserName?.[0] }}</el-avatar>
              <div class="pub-text">
                <span class="pub-label">{{ cat.addUserRole === 'admin' ? '发布方' : '寄养人' }}</span>
                <span class="pub-name">{{ cat.addUserRole === 'admin' ? '平台发布' : cat.addUserName }}</span>
              </div>
            </div>

            <el-descriptions :column="2" border class="info-list">
              <el-descriptions-item label="品种">{{ cat.breed }}</el-descriptions-item>
              <el-descriptions-item label="年龄">{{ cat.age }}岁</el-descriptions-item>
              <el-descriptions-item label="性别">{{ cat.gender }}</el-descriptions-item>
              <el-descriptions-item label="毛色">{{ cat.color }}</el-descriptions-item>
              <el-descriptions-item label="所在地">{{ cat.location }}</el-descriptions-item>
              <el-descriptions-item label="健康状况">{{ cat.healthStatus }}</el-descriptions-item>
              <el-descriptions-item label="疫苗记录">{{ cat.vaccineRecord }}</el-descriptions-item>
              <el-descriptions-item label="绝育状态">{{ cat.isSterilized ? '已绝育' : '未绝育' }}</el-descriptions-item>
            </el-descriptions>

            <p class="desc">{{ cat.description }}</p>

            <div class="actions" v-if="user.id && cat.status === 'available'">
              <el-button v-if="canApply" type="primary" size="large" @click="showApply = true">申请领养</el-button>
              <el-button v-if="cat.addUserId && cat.addUserId !== user.id && cat.addUserRole !== 'admin'" type="success" size="large" @click="contactPublisher">💬 私信寄养人</el-button>
              <span v-else-if="cat.addUserRole === 'admin'" class="admin-hint">📷 官方发布</span>
            </div>
            <div class="actions" v-else-if="!user.id">
              <el-button type="primary" size="large" @click="$router.push('/login')">登录后申请领养</el-button>
            </div>
            <div class="actions" v-else-if="cat.status === 'adopted'">
              <el-tag type="info" size="large">该猫咪已被领养</el-tag>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 评论区 -->
      <el-card class="comment-card">
        <h2>💬 评论区</h2>
        <div class="comment-form" v-if="user.id">
          <el-input v-model="newComment" type="textarea" :rows="3" placeholder="发表评论..." />
          <el-button type="primary" @click="submitComment" :disabled="!newComment.trim()">发表评论</el-button>
        </div>
        <div class="comment-form" v-else>
          <el-button type="primary" @click="$router.push('/login')">登录后发表评论</el-button>
        </div>
        <div class="comment-list">
          <div v-for="comment in comments" :key="comment.id" class="comment-item">
            <el-avatar>{{ comment.nickname?.[0] || 'U' }}</el-avatar>
            <div class="comment-content">
              <div class="comment-header">
                <span class="username">{{ comment.nickname }}</span>
                <div class="comment-meta">
                  <span class="time">{{ formatTime(comment.createTime) }}</span>
                  <el-button
                    v-if="user.id && comment.userId !== user.id"
                    link
                    type="primary"
                    class="comment-chat-btn"
                    @click="contactCommentUser(comment)"
                  >
                    私信
                  </el-button>
                </div>
              </div>
              <p>{{ comment.content }}</p>
            </div>
          </div>
          <el-empty v-if="comments.length === 0" description="暂无评论，快来抢沙发吧"></el-empty>
        </div>
      </el-card>
    </div>

    <!-- 领养申请弹窗 -->
    <el-dialog v-model="showApply" title="领养申请" width="600px">
      <el-form :model="applyForm" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="真实姓名" prop="realName"><el-input v-model="applyForm.realName" /></el-form-item>
        <el-form-item label="联系电话" prop="phone"><el-input v-model="applyForm.phone" /></el-form-item>
        <el-form-item label="职业" prop="occupation"><el-input v-model="applyForm.occupation" /></el-form-item>
        <el-form-item label="居住情况" prop="housing">
          <el-select v-model="applyForm.housing" placeholder="请选择">
            <el-option label="自有住房" value="自有住房" /><el-option label="租房" value="租房" /><el-option label="与父母同住" value="与父母同住" />
          </el-select>
        </el-form-item>
        <el-form-item label="养猫经验" prop="experience"><el-input v-model="applyForm.experience" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="领养原因" prop="reason"><el-input v-model="applyForm.reason" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showApply = false">取消</el-button>
        <el-button type="primary" @click="submitApply">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCatById, getComments, addComment, applyAdoption } from '../api'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const cat = ref(null)
const comments = ref([])
const newComment = ref('')
const user = ref({})
const showApply = ref(false)
const defaultImage = 'https://placekitten.com/400/300'

const applyForm = reactive({ realName: '', phone: '', occupation: '', housing: '', experience: '', reason: '' })
const formRef = ref()
const rules = {
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入电话', trigger: 'blur' }],
  housing: [{ required: true, message: '请选择居住情况', trigger: 'change' }],
  reason: [{ required: true, message: '请填写领养原因', trigger: 'blur' }]
}

// 不能申请自己的猫咪
const canApply = computed(() => {
  return cat.value && cat.value.addUserId !== user.value.id
})

const loadCat = async () => {
  const res = await getCatById(route.params.id)
  if (res.data.code === 200) cat.value = res.data.data
}

const loadComments = async () => {
  const res = await getComments(route.params.id)
  if (res.data.code === 200) comments.value = res.data.data
}

const submitComment = async () => {
  if (!newComment.value.trim()) return
  const res = await addComment({ catId: cat.value.id, userId: user.value.id, content: newComment.value })
  if (res.data.code === 200) { ElMessage.success('评论成功'); newComment.value = ''; loadComments() }
}

const submitApply = async () => {
  await formRef.value.validate()
  const res = await applyAdoption({ catId: cat.value.id, userId: user.value.id, ...applyForm })
  if (res.data.code === 200) { ElMessage.success('申请已提交，等待审核'); showApply.value = false; router.push('/my-adoptions') }
}

const contactPublisher = () => {
  if (cat.value.addUserId) {
    router.push({
      path: '/chat',
      query: {
        otherId: cat.value.addUserId,
        otherName: cat.value.addUserName,
        otherAvatar: cat.value.addUserAvatar,
        catId: cat.value.id,
        catName: cat.value.name
      }
    })
  }
}

const contactCommentUser = (comment) => {
  router.push({
    path: '/chat',
    query: {
      otherId: comment.userId,
      otherName: comment.nickname,
      otherAvatar: comment.avatar,
      catId: cat.value?.id,
      catName: cat.value?.name
    }
  })
}

const formatTime = (time) => time ? new Date(time).toLocaleString() : ''

onMounted(() => {
  try { user.value = JSON.parse(localStorage.getItem('user')) || {} } catch (e) { user.value = {} }
  loadCat()
  loadComments()
})
</script>

<style scoped>
.cat-detail { min-height: 100vh; background: #f5f5f5; }
.navbar { display: flex; padding: 12px 30px; background: white; box-shadow: 0 2px 12px rgba(0,0,0,0.08); }
.logo { font-size: 18px; color: #409eff; cursor: pointer; }
.container { max-width: 1200px; margin: 20px auto; padding: 0 20px; }
.img-card, .info-card, .comment-card { border-radius: 12px; }
.main-img { width: 100%; border-radius: 12px; }
.header { display: flex; align-items: center; gap: 15px; }
.header h1 { margin: 0; }

.publisher-info { display: flex; align-items: center; gap: 12px; margin: 15px 0; padding: 10px; background: #f5f7fa; border-radius: 8px; }
.pub-text { display: flex; flex-direction: column; }
.pub-label { font-size: 12px; color: #999; }
.pub-name { font-size: 15px; font-weight: 500; }

.info-list { margin: 20px 0; }
.desc { color: #666; line-height: 1.8; margin: 15px 0; }
.actions { margin-top: 20px; display: flex; gap: 10px; }

.comment-card { margin-top: 20px; }
.comment-card h2 { margin: 0 0 20px; }
.comment-form { display: flex; gap: 10px; margin-bottom: 20px; }
.comment-form .el-input { flex: 1; }
.comment-list { margin-top: 20px; }
.comment-item { display: flex; gap: 15px; padding: 15px 0; border-bottom: 1px solid #eee; }
.comment-content { flex: 1; }
.comment-header { display: flex; justify-content: space-between; align-items: flex-start; gap: 10px; margin-bottom: 5px; }
.comment-meta { display: flex; align-items: center; gap: 10px; }
.username { font-weight: bold; }
.time { color: #999; font-size: 13px; }
.comment-content p { margin: 0; color: #666; }
.admin-hint { color: #409eff; font-size: 14px; font-style: italic; margin-left: 10px; }
.comment-chat-btn { padding: 0; min-height: auto; }
</style>

