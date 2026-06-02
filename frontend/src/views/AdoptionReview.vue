<template>
  <div class="adoption-review">
    <el-page-header @back="$router.back()" content="领养审核" />

    <div class="filter-bar">
      <el-select v-model="statusFilter" placeholder="筛选状态" clearable @change="loadData" style="width: 150px">
        <el-option label="待审核" value="pending" />
        <el-option label="已通过" value="approved" />
        <el-option label="已拒绝" value="rejected" />
      </el-select>
      <el-button type="primary" @click="loadData">刷新</el-button>
    </div>

    <el-table :data="filteredAdoptions" stripe style="width: 100%" empty-text="暂无领养申请">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="catName" label="猫咪" width="100" />
      <el-table-column prop="userName" label="申请人" width="100" />
      <el-table-column prop="realName" label="真实姓名" width="100" />
      <el-table-column prop="phone" label="联系电话" width="130" />
      <el-table-column prop="occupation" label="职业" width="100" />
      <el-table-column prop="housing" label="居住情况" width="100" />
      <el-table-column prop="experience" label="养猫经验" min-width="150" show-overflow-tooltip />
      <el-table-column prop="reason" label="领养原因" min-width="180" show-overflow-tooltip />
      <el-table-column prop="applyTime" label="申请时间" width="170" />
      <el-table-column prop="status" label="状态" width="100" fixed="right">
        <template #default="{ row }">
          <el-tag v-if="row.status === 'pending'" type="warning">待审核</el-tag>
          <el-tag v-else-if="row.status === 'approved'" type="success">已通过</el-tag>
          <el-tag v-else-if="row.status === 'rejected'" type="danger">已拒绝</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <template v-if="row.status === 'pending'">
            <el-button size="small" type="success" @click="handleApprove(row.id, true)">通过</el-button>
            <el-button size="small" type="danger" @click="handleApprove(row.id, false)">拒绝</el-button>
          </template>
          <span v-else style="color: #999">已处理</span>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getAdoptionList, approveAdoption } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const adoptions = ref([])
const statusFilter = ref('')

const filteredAdoptions = computed(() => {
  if (!statusFilter.value) return adoptions.value
  return adoptions.value.filter(a => a.status === statusFilter.value)
})

const loadData = async () => {
  try {
    const res = await getAdoptionList()
    if (res.data.code === 200) {
      adoptions.value = res.data.data
    }
  } catch (e) { /* ignore */ }
}

const handleApprove = async (id, approved) => {
  const msg = approved ? '确认通过该领养申请？通过后猫咪状态将变为已领养。' : '确认拒绝该领养申请？'
  try {
    await ElMessageBox.confirm(msg, '审核', { type: 'warning' })
  } catch { return }

  try {
    const res = await approveAdoption(id, approved)
    if (res.data.code === 200) {
      ElMessage.success(approved ? '已批准领养' : '已拒绝')
      loadData()
    } else {
      ElMessage.error(res.data.message || '操作失败')
    }
  } catch (e) { /* ignore */ }
}

onMounted(() => { loadData() })
</script>

<style scoped>
.adoption-review {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}
.filter-bar {
  display: flex;
  gap: 10px;
  margin: 20px 0;
}
</style>
