<template>
  <div class="login">
    <el-card class="login-card">
      <h2>登录</h2>
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" @keyup.enter="submit" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width: 100%" @click="submit" :loading="loading">登录</el-button>
        </el-form-item>
      </el-form>
      <div class="footer">
        <span>还没有账号？</span>
        <el-link type="primary" @click="$router.push('/register')">立即注册</el-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '../api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const form = ref({ username: '', password: '' })
const loading = ref(false)
const formRef = ref()

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const submit = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await login(form.value)
    if (res.data.code === 200) {
      const { user, token } = res.data.data
      // 存储 JWT Token 和用户信息
      localStorage.setItem('token', token)
      localStorage.setItem('user', JSON.stringify(user))
      ElMessage.success('登录成功')
      router.push('/')
    } else {
      ElMessage.error(res.data.message)
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f5f5f5;
}

.login-card {
  width: 400px;
  padding: 20px;
}

.login-card h2 {
  text-align: center;
  margin-bottom: 20px;
}

.footer {
  text-align: center;
  color: #666;
}
</style>
