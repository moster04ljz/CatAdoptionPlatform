<template>
  <div class="admin">
    <el-page-header @back="$router.back()" content="管理后台" />

    <el-tabs v-model="activeTab" style="margin-top: 20px">
      <!-- 猫咪管理 -->
      <el-tab-pane label="猫咪管理" name="cat">
        <el-button type="primary" @click="openCatDialog" style="margin-bottom: 15px">+ 添加猫咪</el-button>
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
          <el-table-column prop="age" label="年龄" width="70" />
          <el-table-column prop="gender" label="性别" width="70" />
          <el-table-column prop="addUserName" label="发布者" width="90" />
          <el-table-column prop="status" label="状态" width="110">
            <template #default="{ row }">
              <el-tag v-if="row.status === 'available'" type="success">可领养</el-tag>
              <el-tag v-else-if="row.status === 'adopted'" type="info">已领养</el-tag>
              <el-tag v-else-if="row.status === 'pending_review'" type="warning">待审核</el-tag>
              <el-tag v-else-if="row.status === 'rejected'" type="danger">已拒绝</el-tag>
              <el-tag v-else>{{ row.status }}</el-tag>
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

      <!-- 轮播图管理 -->
      <el-tab-pane label="轮播图管理" name="carousel">
        <el-button type="primary" @click="openCarouselDialog" style="margin-bottom: 15px">+ 添加轮播图</el-button>
        <el-table :data="carousels" empty-text="暂无轮播图">
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column label="图片" width="120">
            <template #default="{ row }">
              <el-image v-if="row.imageUrl" :src="row.imageUrl" style="width:100px;height:56px;border-radius:4px;object-fit:cover" fit="cover" />
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="title" label="标题" width="150" />
          <el-table-column label="关联猫咪" width="120">
            <template #default="{ row }">
              <el-tag v-if="row.catId" type="success">{{ getCatName(row.catId) }}</el-tag>
              <span v-else class="text-muted">无</span>
            </template>
          </el-table-column>
          <el-table-column prop="sortOrder" label="排序" width="80" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'active' ? 'success' : 'info'">{{ row.status === 'active' ? '启用' : '禁用' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button size="small" @click="editCarousel(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDeleteCarousel(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 商品管理 -->
      <el-tab-pane label="商品管理" name="product">
        <el-button type="primary" @click="openProductDialog" style="margin-bottom: 15px">+ 添加商品</el-button>
        <el-table :data="products" empty-text="暂无商品">
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column label="图片" width="80">
            <template #default="{ row }">
              <el-image v-if="row.imageUrl" :src="row.imageUrl" style="width:50px;height:50px;border-radius:4px;object-fit:cover" fit="cover" />
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="name" label="名称" width="150" />
          <el-table-column prop="category" label="分类" width="100" />
          <el-table-column prop="brand" label="品牌" width="100" />
          <el-table-column prop="price" label="价格" width="90" />
          <el-table-column prop="stock" label="库存" width="70" />
          <el-table-column label="图片数" width="80">
            <template #default="{ row }">
              <el-tag v-if="row.imageUrls" type="info" size="small">{{ getProductImageCount(row) }}张</el-tag>
              <span v-else>1</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button size="small" @click="editProduct(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDeleteProduct(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 猫咪编辑对话框 -->
    <el-dialog v-model="catDialog" :title="catForm.id ? '编辑猫咪' : '添加猫咪'" width="600px" @close="resetCatForm">
      <el-form :model="catForm" :rules="catRules" ref="catFormRef" label-width="80px">
        <el-form-item label="名字" prop="name"><el-input v-model="catForm.name" /></el-form-item>
        <el-form-item label="品种" prop="breed">
          <el-select v-model="catForm.breed" style="width:100%">
            <el-option label="中华田园猫" value="Chinese Rural Cat" /><el-option label="英国短毛猫" value="British Shorthair" />
            <el-option label="美国短毛猫" value="American Shorthair" /><el-option label="波斯猫" value="Persian" />
            <el-option label="暹罗猫" value="Siamese" /><el-option label="布偶猫" value="Ragdoll" />
            <el-option label="橘猫" value="Orange" /><el-option label="其他" value="Other" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="年龄"><el-input-number v-model="catForm.age" :min="0" :max="20" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="性别">
            <el-select v-model="catForm.gender" style="width:100%"><el-option label="公" value="Male" /><el-option label="母" value="Female" /></el-select>
          </el-form-item></el-col>
        </el-row>
        <el-form-item label="毛色"><el-input v-model="catForm.color" /></el-form-item>
        <el-form-item label="所在地"><el-input v-model="catForm.location" /></el-form-item>
        <el-form-item label="简介"><el-input v-model="catForm.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="图片">
          <el-upload action="/api/upload/image" :show-file-list="false" :on-success="cat => catForm.imageUrl = cat.data" :before-upload="beforeUpload" accept="image/*">
            <img v-if="catForm.imageUrl" :src="catForm.imageUrl" style="width:120px;height:120px;object-fit:cover;border-radius:8px" />
            <el-button v-else>上传图片</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="catDialog = false">取消</el-button>
        <el-button type="primary" @click="saveCat" :loading="saving">保存</el-button>
      </template>
    </el-dialog>

    <!-- 轮播图编辑对话框 - 从猫咪选择 -->
    <el-dialog v-model="carouselDialog" :title="carouselForm.id ? '编辑轮播图' : '添加轮播图'" width="600px" @close="resetCarouselForm">
      <el-form :model="carouselForm" ref="carouselFormRef" label-width="80px">
        <el-form-item label="选择猫咪" required>
          <el-select v-model="carouselForm.catId" filterable placeholder="搜索并选择一只猫咪" style="width:100%" clearable @change="onCarouselCatChange">
            <el-option v-for="c in availableCats" :key="c.id" :label="`${c.name} (${c.breed})`" :value="c.id">
              <div style="display:flex;align-items:center;gap:8px">
                <img v-if="c.imageUrl" :src="c.imageUrl" style="width:30px;height:30px;border-radius:4px;object-fit:cover" />
                <span>{{ c.name }} - {{ c.breed }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="carouselForm.title" placeholder="留空则使用猫咪名字" />
        </el-form-item>
        <el-form-item label="轮播图片">
          <el-upload action="/api/upload/image" :show-file-list="false" :on-success="res => carouselForm.imageUrl = res.data" :before-upload="beforeUpload" accept="image/*">
            <img v-if="carouselForm.imageUrl" :src="carouselForm.imageUrl" style="width:200px;height:112px;object-fit:cover;border-radius:8px" />
            <div v-else class="upload-hint">
              <el-button>上传图片（建议1200x400）</el-button>
              <p class="text-muted" style="margin-top:5px;font-size:12px">选择猫咪后会自动使用猫咪图片</p>
            </div>
          </el-upload>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="排序"><el-input-number v-model="carouselForm.sortOrder" :min="0" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态">
            <el-select v-model="carouselForm.status" style="width:100%"><el-option label="启用" value="active" /><el-option label="禁用" value="inactive" /></el-select>
          </el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="carouselDialog = false">取消</el-button>
        <el-button type="primary" @click="saveCarousel" :loading="saving">保存</el-button>
      </template>
    </el-dialog>

    <!-- 商品编辑对话框 - 支持多图 -->
    <el-dialog v-model="productDialog" :title="productForm.id ? '编辑商品' : '添加商品'" width="650px" @close="resetProductForm">
      <el-form :model="productForm" :rules="productRules" ref="productFormRef" label-width="80px">
        <el-form-item label="名称" prop="name"><el-input v-model="productForm.name" /></el-form-item>
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="分类">
            <el-select v-model="productForm.category" style="width:100%">
              <el-option label="猫粮" value="cat_food" /><el-option label="猫条/零食" value="cat_treat" />
              <el-option label="猫砂" value="cat_litter" /><el-option label="玩具" value="cat_toy" />
              <el-option label="猫窝" value="cat_bed" /><el-option label="其他" value="cat_other" />
            </el-select>
          </el-form-item></el-col>
          <el-col :span="12"><el-form-item label="品牌"><el-input v-model="productForm.brand" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="价格"><el-input-number v-model="productForm.price" :min="0" :step="1" :precision="2" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="库存"><el-input-number v-model="productForm.stock" :min="0" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="描述"><el-input v-model="productForm.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="商品图片">
          <div class="product-images">
            <div class="image-item" v-for="(img, idx) in productImages" :key="idx">
              <img :src="img" class="thumb" />
              <el-button class="remove-btn" type="danger" circle size="small" @click="removeImage(idx)" v-if="idx > 0">
                <el-icon><Close /></el-icon>
              </el-button>
              <span class="img-label" v-if="idx === 0">主图</span>
            </div>
            <el-upload
              v-if="productImages.length < 8"
              action="/api/upload/image"
              :show-file-list="false"
              :on-success="onProductImageUpload"
              :before-upload="beforeUpload"
              accept="image/*"
              class="add-image-btn"
            >
              <div class="upload-placeholder">
                <el-icon :size="24"><Plus /></el-icon>
                <span>添加图片</span>
                <span class="text-muted">{{ productImages.length }}/8</span>
              </div>
            </el-upload>
          </div>
          <p class="text-muted" style="margin-top:5px;font-size:12px">第一张为主图，最多上传8张图片</p>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="productDialog = false">取消</el-button>
        <el-button type="primary" @click="saveProduct" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { getCatAdminAll, addCat, updateCat, deleteCat, approveCat, getCarouselList, addCarousel, updateCarousel, deleteCarousel, getProductList, addProduct, updateProduct, deleteProduct } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Close, Plus } from '@element-plus/icons-vue'

const activeTab = ref('cat')
const cats = ref([])
const carousels = ref([])
const products = ref([])
const saving = ref(false)
const catDialog = ref(false)
const carouselDialog = ref(false)
const productDialog = ref(false)
const catFormRef = ref()
const carouselFormRef = ref()
const productFormRef = ref()

// ====== 猫咪 ======
const catForm = reactive({ id: null, name: '', breed: '', age: 1, gender: 'Male', color: '', description: '', imageUrl: '', category: 'adoption', location: '', addUserId: null, isHot: false })
const catRules = { name: [{ required: true, message: '请输入名字', trigger: 'blur' }], breed: [{ required: true, message: '请选择品种', trigger: 'change' }] }

const openCatDialog = () => { Object.assign(catForm, { id: null, name: '', breed: '', age: 1, gender: 'Male', color: '', description: '', imageUrl: '', category: 'adoption', location: '', addUserId: null, isHot: false }); catDialog.value = true }
const resetCatForm = () => catFormRef.value?.resetFields()
const editCat = (row) => { Object.assign(catForm, row); catDialog.value = true }

const saveCat = async () => {
  await catFormRef.value.validate()
  saving.value = true
  try {
    const res = catForm.id ? await updateCat(catForm) : await addCat(catForm)
    if (res.data.code === 200) { ElMessage.success('保存成功'); catDialog.value = false; loadCats() }
    else ElMessage.error(res.data.message || '操作失败')
  } finally { saving.value = false }
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除该猫咪？删除后不可恢复。', '提示', { type: 'warning' })
  try {
    const res = await deleteCat(id)
    if (res.data.code === 200) { ElMessage.success('删除成功'); loadCats() }
    else ElMessage.error(res.data.message || '删除失败')
  } catch { /* cancel */ }
}

const handleApprove = async (cat) => {
  await ElMessageBox.confirm(`确认通过「${cat.name}」的审核？`, '审核', { type: 'info' })
  try {
    const res = await approveCat(cat.id, true)
    if (res.data.code === 200) { ElMessage.success('审核通过'); loadCats() }
  } catch { /* cancel */ }
}

// ====== 轮播图 ======
const carouselForm = reactive({ id: null, imageUrl: '', title: '', catId: null, linkUrl: '', sortOrder: 0, status: 'active' })

// 可选的猫咪列表（用于轮播图选择）
const availableCats = computed(() => cats.value.filter(c => c.status === 'available' && c.imageUrl))

const getCatName = (catId) => {
  const cat = cats.value.find(c => c.id === catId)
  return cat ? cat.name : `#${catId}`
}

const onCarouselCatChange = (catId) => {
  if (catId) {
    const cat = cats.value.find(c => c.id === catId)
    if (cat) {
      carouselForm.imageUrl = cat.imageUrl
      carouselForm.title = carouselForm.title || cat.name
    }
  }
}

const nextSortOrder = computed(() => {
  if (carousels.value.length === 0) return 1
  return Math.max(...carousels.value.map(c => c.sortOrder || 0)) + 1
})

const openCarouselDialog = () => { Object.assign(carouselForm, { id: null, imageUrl: '', title: '', catId: null, linkUrl: '', sortOrder: nextSortOrder.value, status: 'active' }); carouselDialog.value = true }
const resetCarouselForm = () => {}
const editCarousel = (row) => { Object.assign(carouselForm, row); carouselDialog.value = true }

const saveCarousel = async () => {
  saving.value = true
  try {
    const res = carouselForm.id ? await updateCarousel(carouselForm) : await addCarousel(carouselForm)
    if (res.data.code === 200) { ElMessage.success('保存成功'); carouselDialog.value = false; loadCarousels() }
    else ElMessage.error(res.data.message || '操作失败')
  } finally { saving.value = false }
}

const handleDeleteCarousel = async (id) => {
  await ElMessageBox.confirm('确定删除该轮播图？', '提示', { type: 'warning' })
  try {
    const res = await deleteCarousel(id)
    if (res.data.code === 200) { ElMessage.success('删除成功'); loadCarousels() }
  } catch { /* cancel */ }
}

// ====== 商品 ======
const productForm = reactive({ id: null, name: '', category: 'cat_food', brand: '', price: 0, stock: 0, imageUrl: '', imageUrls: '', description: '', status: 'available' })
const productImages = ref([]) // 管理多图URL数组
const productRules = { name: [{ required: true, message: '请输入名称', trigger: 'blur' }] }

const getProductImageCount = (p) => {
  try { return (JSON.parse(p.imageUrls || '[]')).length + 1 } catch { return 1 }
}

const openProductDialog = () => {
  Object.assign(productForm, { id: null, name: '', category: 'cat_food', brand: '', price: 0, stock: 0, imageUrl: '', imageUrls: '', description: '', status: 'available' })
  productImages.value = []
  productDialog.value = true
}

const resetProductForm = () => productFormRef.value?.resetFields()

const editProduct = (row) => {
  Object.assign(productForm, row)
  // 解析多图
  const images = []
  if (row.imageUrl) images.push(row.imageUrl)
  if (row.imageUrls) {
    try { JSON.parse(row.imageUrls).forEach(url => { if (url) images.push(url) }) } catch { /* ignore */ }
  }
  productImages.value = images
  productDialog.value = true
}

const onProductImageUpload = (res) => {
  if (res.code === 200) {
    const url = res.data
    if (productImages.value.length === 0) {
      // 第一张自动设为主图
      productImages.value.push(url)
      productForm.imageUrl = url
    } else if (productImages.value.length < 8) {
      productImages.value.push(url)
    } else {
      ElMessage.warning('最多上传8张图片')
    }
  }
}

const removeImage = (idx) => {
  if (idx === 0) return // 主图不能删除（通过上传替换）
  productImages.value.splice(idx, 1)
}

const saveProduct = async () => {
  await productFormRef.value.validate()
  if (productImages.value.length === 0) {
    ElMessage.warning('请至少上传一张图片')
    return
  }
  // 第一张为主图，其余存入imageUrls JSON
  productForm.imageUrl = productImages.value[0]
  productForm.imageUrls = JSON.stringify(productImages.value.slice(1))
  saving.value = true
  try {
    const res = productForm.id ? await updateProduct(productForm) : await addProduct(productForm)
    if (res.data.code === 200) { ElMessage.success('保存成功'); productDialog.value = false; loadProducts() }
    else ElMessage.error(res.data.message || '操作失败')
  } finally { saving.value = false }
}

const handleDeleteProduct = async (id) => {
  await ElMessageBox.confirm('确定删除该商品？', '提示', { type: 'warning' })
  try {
    const res = await deleteProduct(id)
    if (res.data.code === 200) { ElMessage.success('删除成功'); loadProducts() }
  } catch { /* cancel */ }
}

// ====== 通用 ======
const beforeUpload = (file) => {
  if (!file.type.startsWith('image/')) { ElMessage.error('只能上传图片'); return false }
  if (file.size / 1024 / 1024 > 5) { ElMessage.error('图片不能超过5MB'); return false }
  return true
}

// ====== 数据加载 ======
const loadCats = async () => { try { const res = await getCatAdminAll(); if (res.data.code === 200) cats.value = res.data.data } catch { /* ignore */ } }
const loadCarousels = async () => { try { const res = await getCarouselList(); if (res.data.code === 200) carousels.value = res.data.data } catch { /* ignore */ } }
const loadProducts = async () => { try { const res = await getProductList(); if (res.data.code === 200) products.value = res.data.data } catch { /* ignore */ } }

onMounted(() => { loadCats(); loadCarousels(); loadProducts() })
</script>

<style scoped>
.admin { max-width: 1400px; margin: 0 auto; padding: 20px; }
.text-muted { color: #999; }

/* 商品多图管理 */
.product-images { display: flex; flex-wrap: wrap; gap: 10px; }
.image-item { position: relative; width: 100px; height: 100px; border-radius: 8px; overflow: hidden; border: 2px solid #dcdfe6; }
.image-item .thumb { width: 100%; height: 100%; object-fit: cover; }
.image-item .remove-btn { position: absolute; top: 2px; right: 2px; }
.image-item .img-label { position: absolute; bottom: 0; left: 0; right: 0; text-align: center; font-size: 11px; color: white; background: rgba(0,0,0,0.5); }
.add-image-btn { width: 100px; height: 100px; }
.upload-placeholder { width: 100%; height: 100%; display: flex; flex-direction: column; align-items: center; justify-content: center; border: 2px dashed #dcdfe6; border-radius: 8px; color: #999; cursor: pointer; }
.upload-placeholder:hover { border-color: #409eff; color: #409eff; }

.upload-hint p { margin: 0; }
</style>
