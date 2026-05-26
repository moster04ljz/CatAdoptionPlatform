<template>
  <div class="add-cat">
    <div class="navbar">
      <div class="logo" @click="$router.push('/')">🐱 返回首页</div>
      <el-button @click="$router.push('/my-cats')">我发布的猫咪</el-button>
    </div>

    <div class="container">
      <el-card>
        <h2>{{ editing ? '编辑猫咪信息' : '🐱 发布猫咪' }}</h2>
        <p class="tip" v-if="isAdmin">发布猫咪（管理员直接上架）</p>
        <p class="tip" v-else>提交后需要管理员审核，审核通过后将上架到领养队列</p>

        <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
          <el-form-item label="昵称" prop="name">
            <el-input v-model="form.name" placeholder="给它取个名字" />
          </el-form-item>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="品种" prop="breed">
                <el-select v-model="form.breed" placeholder="选择品种">
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
            </el-col>
            <el-col :span="12">
              <el-form-item label="类型" prop="category">
                <el-radio-group v-model="form.category">
                  <el-radio label="adoption">免费领养</el-radio>
                  <el-radio label="market">商城出售</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="年龄" prop="age">
                <el-input-number v-model="form.age" :min="0" :max="20" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="性别" prop="gender">
                <el-select v-model="form.gender" placeholder="选择性别">
                  <el-option label="公" value="Male" />
                  <el-option label="母" value="Female" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="毛色" prop="color">
                <el-input v-model="form.color" placeholder="如：橘色、白色" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="价格" prop="price" v-if="form.category === 'market'">
                <el-input-number v-model="form.price" :min="0" :step="50" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="所在地" prop="location">
                <el-input v-model="form.location" placeholder="如：北京" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="上传图片" prop="imageUrl">
            <el-upload
              class="avatar-uploader"
              :action="uploadUrl"
              :show-file-list="false"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
              :before-upload="beforeUpload"
              accept="image/*">
              <img v-if="form.imageUrl" :src="form.imageUrl" class="avatar-preview" @error="imgError" />
              <el-icon v-else class="upload-icon"><Plus /></el-icon>
            </el-upload>
            <div class="upload-tip">点击上传猫咪照片，支持 jpg/png 格式</div>
          </el-form-item>

          <el-form-item label="健康状况" prop="healthStatus">
            <el-checkbox-group v-model="healthChecks">
              <el-checkbox label="健康">健康</el-checkbox>
              <el-checkbox label="已接种疫苗">已接种疫苗</el-checkbox>
              <el-checkbox label="已绝育">已绝育</el-checkbox>
              <el-checkbox label="已驱虫">已驱虫</el-checkbox>
            </el-checkbox-group>
          </el-form-item>

          <el-form-item label="详细介绍" prop="description">
            <el-input v-model="form.description" type="textarea" :rows="4" placeholder="描述猫咪的性格、习惯、经历等..." />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="submit" :loading="loading">提交审核</el-button>
            <el-button @click="$router.back()">取消</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 我发布的猫咪列表 -->
      <el-card class="my-cats" v-if="myCats.length > 0">
        <h3>我发布的猫咪</h3>
        <el-table :data="myCats" stripe>
          <el-table-column prop="name" label="昵称" />
          <el-table-column prop="breed" label="品种" />
          <el-table-column prop="category" label="类型">
            <template #default="{ row }">
              {{ row.category === 'market' ? '商城出售' : '免费领养' }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态">
            <template #default="{ row }">
              <el-tag v-if="row.status === 'available'" type="success">已通过</el-tag>
              <el-tag v-else-if="row.status === 'pending_review'" type="warning">待审核</el-tag>
              <el-tag v-else type="info">{{ row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150">
            <template #default="{ row }">
              <el-button link type="primary" @click="edit(row)">编辑</el-button>
              <el-button link type="danger" @click="remove(row.id)" v-if="row.status !== 'available'">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { addCat, updateCat, deleteCat, getMyCats, uploadImage } from '../api'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const editing = ref(false)
const myCats = ref([])
const healthChecks = ref([])
const uploadUrl = '/api/upload/image'
const isAdmin = ref(false)

const form = reactive({
  name: '', breed: '', age: 1, gender: '', color: '', description: '',
  imageUrl: '', category: 'adoption', price: 0, location: '',
  healthStatus: '', vaccineRecord: '', isSterilized: false, isHot: false
})

const formRef = ref()
const rules = {
  name: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  breed: [{ required: true, message: '请选择品种', trigger: 'change' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  location: [{ required: true, message: '请输入所在地', trigger: 'blur' }]
}

watch(healthChecks, (vals) => {
  form.healthStatus = vals.filter(v => v === '健康').join(',')
  form.vaccineRecord = vals.filter(v => v === '已接种疫苗' || v === '已驱虫').join(',')
  form.isSterilized = vals.includes('已绝育')
})

const imgError = (e) => {
  e.target.src = 'https://placekitten.com/300/200'
}

const handleUploadSuccess = (response) => {
  if (response.code === 200) {
    form.imageUrl = response.data
    ElMessage.success('上传成功')
  }
}

const handleUploadError = () => {
  ElMessage.error('上传失败')
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

const submit = async () => {
  await formRef.value.validate()
  loading.value = true
  const user = (() => { try { return JSON.parse(localStorage.getItem('user')) || {} } catch(e) { return {} } })()
  form.addUserId = user.id

  // 管理员发布免审核
  if (user.role === 'admin') {
    form.status = 'available'
  } else {
    form.status = 'pending'
  }

  const res = editing.value
    ? await updateCat(form)
    : await addCat(form)

  loading.value = false
  if (res.data.code === 200) {
    ElMessage.success(editing.value ? '更新成功' : (user.role === 'admin' ? '发布成功' : '提交成功，等待审核'))
    if (editing.value) editing.value = false
    loadMyCats()
  }
}

const edit = (cat) => {
  Object.assign(form, cat)
  editing.value = true
}

const remove = async (id) => {
  await deleteCat(id)
  ElMessage.success('删除成功')
  loadMyCats()
}

const loadMyCats = async () => {
  const user = (() => { try { return JSON.parse(localStorage.getItem('user')) || {} } catch(e) { return {} } })()
  const res = await getMyCats(user.id)
  if (res.data.code === 200) {
    myCats.value = res.data.data
  }
}

onMounted(() => {
  const user = (() => { try { return JSON.parse(localStorage.getItem('user')) || {} } catch(e) { return {} } })()
  isAdmin.value = user.role === 'admin'
  loadMyCats()
})
</script>

<style scoped>
.add-cat { min-height: 100vh; background: #f5f5f5; }
.navbar { display: flex; justify-content: space-between; padding: 12px 30px; background: white; box-shadow: 0 2px 12px rgba(0,0,0,0.08); }
.logo { font-size: 18px; color: #409eff; cursor: pointer; }
.container { max-width: 800px; margin: 20px auto; padding: 0 20px; }
.container h2 { margin: 0 0 10px; }
.tip { color: #e6a23c; margin-bottom: 20px; }
.my-cats { margin-top: 30px; }
.my-cats h3 { margin: 0 0 15px; }

.avatar-uploader { display: inline-block; }
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
  margin-top: 8px;
}
</style>
