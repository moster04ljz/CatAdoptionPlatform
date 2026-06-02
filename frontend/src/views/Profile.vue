<template>
  <div class="profile">
    <el-page-header @back="$router.back()" content="个人中心" />

    <el-card class="profile-card">
      <div class="user-info">
        <el-upload
          class="avatar-uploader"
          :action="uploadUrl"
          :show-file-list="false"
          :on-success="handleUploadSuccess"
          :before-upload="beforeUpload"
          accept="image/*">
          <el-avatar v-if="user.avatar" :size="80" :src="user.avatar">
            {{ user.nickname?.[0] }}
          </el-avatar>
          <el-avatar v-else :size="80">{{ user.nickname?.[0] || 'U' }}</el-avatar>
        </el-upload>
        <div class="info">
          <h2>{{ user.nickname }}</h2>
          <p>{{ user.email }}</p>
          <p>{{ user.phone }}</p>
        </div>
      </div>

      <el-divider />

      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="手机" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveProfile">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="stats-card">
      <h3>领养统计</h3>
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="stat-item">
            <span class="num">{{ stats.pending }}</span>
            <span class="label">待审核</span>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-item">
            <span class="num success">{{ stats.approved }}</span>
            <span class="label">已通过</span>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-item">
            <span class="num danger">{{ stats.rejected }}</span>
            <span class="label">已拒绝</span>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getUserInfo, updateUser, getAdoptionsByUser } from '../api'
import { ElMessage } from 'element-plus'

const user = ref({})
const form = ref({ nickname: '', email: '', phone: '', avatar: '' })
const formRef = ref()
const stats = reactive({ pending: 0, approved: 0, rejected: 0 })
const uploadUrl = '/api/upload/image'

const rules = {
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }]
}

const loadUser = async () => {
  const userData = (() => { try { return JSON.parse(localStorage.getItem('user')) || {} } catch(e) { return {} } })()
  if (userData.id) {
    const res = await getUserInfo(userData.id)
    if (res.data.code === 200) {
      user.value = res.data.data
      form.value = { ...res.data.data }
    }
  }
}

const loadStats = async () => {
  const userData = (() => { try { return JSON.parse(localStorage.getItem('user')) || {} } catch(e) { return {} } })()
  if (userData.id) {
    const res = await getAdoptionsByUser(userData.id)
    if (res.data.code === 200) {
      const list = res.data.data
      stats.pending = list.filter(a => a.status === 'pending').length
      stats.approved = list.filter(a => a.status === 'approved').length
      stats.rejected = list.filter(a => a.status === 'rejected').length
    }
  }
}

const saveProfile = async () => {
  await formRef.value.validate()
  const res = await updateUser(form.value)
  if (res.data.code === 200) {
    ElMessage.success('保存成功')
    const updated = { ...user.value, nickname: form.value.nickname, email: form.value.email, phone: form.value.phone, avatar: form.value.avatar }
    localStorage.setItem('user', JSON.stringify(updated))
    user.value = updated
  }
}

const handleUploadSuccess = (response) => {
  if (response.code === 200) {
    form.value.avatar = response.data
    user.value.avatar = response.data
    ElMessage.success('上传成功')
  }
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isImage) {
    ElMessage.error('只能上传图片')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片不能超过2MB')
    return false
  }
  return true
}

onMounted(() => {
  loadUser()
  loadStats()
})
</script>

<style scoped>
.profile {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

.profile-card, .stats-card {
  margin-top: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.info h2 {
  margin: 0 0 10px;
}

.info p {
  color: #666;
  margin: 5px 0;
}

.avatar-uploader {
  cursor: pointer;
}
.avatar-uploader:hover {
  opacity: 0.8;
}

.stat-item {
  text-align: center;
  padding: 20px;
  background: #f5f5f5;
  border-radius: 8px;
}

.stat-item .num {
  display: block;
  font-size: 32px;
  font-weight: bold;
  color: #e6a23c;
}

.stat-item .num.success {
  color: #67c23a;
}

.stat-item .num.danger {
  color: #f56c6c;
}

.stat-item .label {
  display: block;
  margin-top: 5px;
  color: #999;
}
</style>
