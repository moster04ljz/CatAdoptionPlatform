<template>
  <div class="admin">
    <el-page-header @back="$router.back()" content="管理后台" />
    
    <el-tabs v-model="activeTab" style="margin-top: 20px">
      <el-tab-pane label="猫咪管理" name="cat">
        <el-button type="primary" @click="openAddDialog" style="margin-bottom: 15px">+ 添加猫咪</el-button>
        
        <el-table :data="cats" empty-text="暂无猫咪">
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column label="图片" width="80">
            <template #default="{ row }">
              <el-image v-if="row.imageUrl" :src="row.imageUrl" style="width:50px;height:50px;border-radius:4px;object-fit:cover" fit="cover" />
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="name" label="名字" width="100" />
          <el-table-column prop="breed" label="品种" width="120" />
          <el-table-column prop="age" label="年龄" width="80" />
          <el-table-column prop="gender" label="性别" width="80" />
          <el-table-column prop="status" label="状态" width="120">
            <template #default="{ row }">
              <el-tag v-if="row.status === 'available'" type="success">可领养</el-tag>
              <el-tag v-else-if="row.status === 'adopted'" type="info">已领养</el-tag>
              <el-tag v-else-if="row.status === 'pending_review'" type="warning">待审核</el-tag>
              <el-tag v-else-if="row.status === 'rejected'" type="danger">已拒绝</el-tag>
              <el-tag v-else>{{ row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="category" label="类型" width="80">
            <template #default="{ row }">
              {{ row.category === 'market' ? '出售' : '领养' }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button size="small" @click="handleApprove(row)" v-if="row.status === 'pending_review'">审核</el-button>
              <el-button size="small" @click="editCat(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      
      <el-tab-pane label="领养审核" name="adoption">
        <el-table :data="adoptions" empty-text="暂无申请">
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="catName" label="猫咪" width="100" />
          <el-table-column prop="userName" label="申请人" width="100" />
          <el-table-column prop="realName" label="真实姓名" width="100" />
          <el-table-column prop="phone" label="联系电话" width="120" />
          <el-table-column prop="occupation" label="职业" width="100" />
          <el-table-column prop="housing" label="居住情况" width="100" />
          <el-table-column prop="applyTime" label="申请时间" width="180" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag v-if="row.status === 'pending'" type="warning">待审核</el-tag>
              <el-tag v-else-if="row.status === 'approved'" type="success">已通过</el-tag>
              <el-tag v-else-if="row.status === 'rejected'" type="danger">已拒绝</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button v-if="row.status === 'pending'" size="small" type="success" @click="approve(row.id, true)">通过</el-button>
              <el-button v-if="row.status === 'pending'" size="small" type="danger" @click="approve(row.id, false)">拒绝</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
    
    <!-- 添加/编辑猫咪对话框 -->
    <el-dialog v-model="addDialog" :title="editForm.id ? '编辑猫咪' : '添加猫咪'" width="600px" @close="resetForm">
      <el-form :model="editForm" :rules="formRules" ref="formRef" label-width="80px">
        <el-form-item label="名字" prop="name">
          <el-input v-model="editForm.name" />
        </el-form-item>
        <el-form-item label="品种" prop="breed">
          <el-select v-model="editForm.breed" placeholder="选择品种" style="width:100%">
            <el-option label="中华田园猫" value="Chinese Rural Cat" />
            <el-option label="英国短毛猫" value="British Shorthair" />
            <el-option label="美国短毛猫" value="American Shorthair" />
            <el-option label="波斯猫" value="Persian" />
            <el-option label="暹罗猫" value="Siamese" />
            <el-option label="布偶猫" value="Ragdoll" />
            <el-option label="橘猫" value="Orange" />
            <el-option label="其他" value="Other" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="年龄" prop="age">
              <el-input-number v-model="editForm.age" :min="0" :max="20" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="editForm.gender" style="width:100%">
                <el-option label="公" value="Male" />
                <el-option label="母" value="Female" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="毛色" prop="color">
          <el-input v-model="editForm.color" />
        </el-form-item>
        <el-form-item label="类型" prop="category">
          <el-radio-group v-model="editForm.category">
            <el-radio label="adoption">免费领养</el-radio>
            <el-radio label="market">商城出售</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="价格" v-if="editForm.category === 'market'">
          <el-input-number v-model="editForm.price" :min="0" :step="50" />
        </el-form-item>
        <el-form-item label="所在地">
          <el-input v-model="editForm.location" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="editForm.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="图片">
          <div class="upload-area">
            <el-upload
              class="avatar-uploader"
              action="/api/upload/image"
              :show-file-list="false"
              :on-success="handleUploadSuccess"
              :before-upload="beforeUpload"
              accept="image/*">
              <img v-if="editForm.imageUrl" :src="editForm.imageUrl" class="avatar-preview" />
              <el-icon v-else class="upload-icon"><Plus /></el-icon>
            </el-upload>
            <div class="upload-tip">点击上传猫咪照片，支持 jpg/png，最大 5MB</div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialog = false">取消</el-button>
        <el-button type="primary" @click="saveCat" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getCatAdminAll, addCat, updateCat, deleteCat, approveCat, getAdoptionList, approveAdoption } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const activeTab = ref('cat')
const cats = ref([])
const adoptions = ref([])
const addDialog = ref(false)
const saving = ref(false)
const formRef = ref()

const defaultForm = {
  id: null, name: '', breed: '', age: 1, gender: 'Male', color: '',
  description: '', imageUrl: '', category: 'adoption', price: 0, location: '',
  healthStatus: '', vaccineRecord: '', isSterilized: false, isHot: false,
  addUserId: null
}
const editForm = reactive({ ...defaultForm })

const formRules = {
  name: [{ required: true, message: '请输入名字', trigger: 'blur' }],
  breed: [{ required: true, message: '请选择品种', trigger: 'change' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }]
}

const loadCats = async () => {
  try {
    const res = await getCatAdminAll()
    if (res.data.code === 200) {
      cats.value = res.data.data
    }
  } catch (e) {
    // 可能无权限（非管理员访问公开接口）
  }
}

const loadAdoptions = async () => {
  try {
    const res = await getAdoptionList()
    if (res.data.code === 200) {
      adoptions.value = res.data.data
    }
  } catch (e) { /* ignore */ }
}

const openAddDialog = () => {
  Object.assign(editForm, { ...defaultForm })
  addDialog.value = true
}

const resetForm = () => {
  formRef.value?.resetFields()
}

const editCat = (cat) => {
  Object.assign(editForm, cat)
  addDialog.value = true
}

const handleUploadSuccess = (response) => {
  if (response.code === 200) {
    editForm.imageUrl = response.data
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB')
    return false
  }
  return true
}

const saveCat = async () => {
  await formRef.value.validate()
  saving.value = true
  try {
    let res
    if (editForm.id) {
      res = await updateCat(editForm)
    } else {
      res = await addCat(editForm)
    }
    if (res.data.code === 200) {
      ElMessage.success(res.data.message || '保存成功')
      addDialog.value = false
      loadCats()
    } else {
      ElMessage.error(res.data.message || '操作失败')
    }
  } finally {
    saving.value = false
  }
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除该猫咪？', '提示', { type: 'warning' })
  try {
    const res = await deleteCat(id)
    if (res.data.code === 200) {
      ElMessage.success('删除成功')
      loadCats()
    } else {
      ElMessage.error(res.data.message || '删除失败')
    }
  } catch (e) { /* user cancelled */ }
}

const handleApprove = async (cat) => {
  await ElMessageBox.confirm(`确认通过「${cat.name}」的审核？`, '审核', { type: 'info' })
  try {
    const res = await approveCat(cat.id, true)
    if (res.data.code === 200) {
      ElMessage.success('审核通过')
      loadCats()
    } else {
      ElMessage.error(res.data.message || '操作失败')
    }
  } catch (e) { /* user cancelled */ }
}

const approve = async (id, approved) => {
  try {
    const res = await approveAdoption(id, approved)
    if (res.data.code === 200) {
      ElMessage.success(approved ? '已批准' : '已拒绝')
      loadAdoptions()
      loadCats()
    } else {
      ElMessage.error(res.data.message || '操作失败')
    }
  } catch (e) { /* ignore */ }
}

onMounted(() => {
  loadCats()
  loadAdoptions()
})
</script>

<style scoped>
.admin {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

.upload-area {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.avatar-uploader {
  display: inline-block;
}

.avatar-uploader .el-upload {
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 180px;
  height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: border-color 0.3s;
}

.avatar-uploader .el-upload:hover {
  border-color: #409eff;
}

.avatar-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 6px;
}

.upload-icon {
  font-size: 32px;
  color: #8c939d;
}

.upload-tip {
  color: #999;
  font-size: 12px;
}
</style>
