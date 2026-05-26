<template>
  <div class="my-adoptions">
    <div class="navbar">
      <div class="logo" @click="$router.push('/')">🐱 返回首页</div>
    </div>
    
    <div class="container">
      <h1>我的领养申请</h1>
      
      <el-tabs v-model="activeTab">
        <el-tab-pane label="全部申请" name="all">
          <el-table :data="adoptions" stripe>
            <el-table-column prop="catName" label="猫咪" />
            <el-table-column prop="realName" label="申请人" />
            <el-table-column prop="phone" label="电话" />
            <el-table-column prop="housing" label="居住情况" />
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag v-if="row.status === 'pending'" type="warning">待审核</el-tag>
                <el-tag v-else-if="row.status === 'approved'" type="success">已通过</el-tag>
                <el-tag v-else type="danger">已拒绝</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="applyTime" label="申请时间">
              <template #default="{ row }">
                {{ formatTime(row.applyTime) }}
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
      
      <el-empty v-if="adoptions.length === 0" description="暂无申请记录"></el-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAdoptionsByUser } from '../api'

const adoptions = ref([])
const activeTab = ref('all')
const user = ref({})

const load = async () => {
  const res = await getAdoptionsByUser(user.value.id)
  if (res.data.code === 200) {
    adoptions.value = res.data.data
  }
}

const formatTime = (time) => time ? new Date(time).toLocaleString() : ''

onMounted(() => {
  try { user.value = JSON.parse(localStorage.getItem('user')) || {} } catch(e) { user.value = {} }
  load()
})
</script>

<style scoped>
.my-adoptions { min-height: 100vh; background: #f5f5f5; }
.navbar { display: flex; padding: 12px 30px; background: white; box-shadow: 0 2px 12px rgba(0,0,0,0.08); }
.logo { font-size: 18px; color: #409eff; cursor: pointer; }
.container { max-width: 1000px; margin: 20px auto; padding: 20px; }
h1 { margin: 0 0 20px; }
</style>