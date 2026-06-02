<template>
  <div class="product-detail">
    <div class="navbar">
      <div class="logo" @click="$router.push('/shop')">🛒 猫咪用品</div>
      <el-button link @click="$router.back()">返回</el-button>
    </div>

    <div class="container" v-if="product">
      <el-row :gutter="30">
        <el-col :span="12">
          <el-card class="img-card">
            <img :src="currentImage || product.imageUrl || defaultImg" class="main-img" />
            <div class="thumb-list" v-if="allImages.length > 1">
              <img
                v-for="(img, idx) in allImages"
                :key="idx"
                :src="img"
                :class="['thumb', { active: currentImage === img }]"
                @click="currentImage = img"
              />
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="info-card">
            <h1>{{ product.name }}</h1>
            <p class="brand">{{ product.brand }}</p>
            <div class="price">¥{{ product.price }}</div>
            <div class="stock-info">
              <el-tag :type="product.stock > 0 ? 'success' : 'danger'">
                {{ product.stock > 0 ? `库存 ${product.stock}` : '已售罄' }}
              </el-tag>
            </div>
            <el-descriptions :column="1" border class="info-list" v-if="product.description">
              <el-descriptions-item label="商品描述">{{ product.description }}</el-descriptions-item>
              <el-descriptions-item label="分类">{{ categoryLabel }}</el-descriptions-item>
            </el-descriptions>

            <div class="actions">
              <el-input-number v-model="quantity" :min="1" :max="product.stock" />
              <el-button type="warning" size="large" :disabled="product.stock <= 0" @click="handleBuy">
                🛒 联系购买
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 商品图片详情 -->
      <el-card class="gallery-card" v-if="allImages.length > 1">
        <h2>商品图片</h2>
        <div class="gallery-grid">
          <img v-for="(img, idx) in allImages" :key="idx" :src="img" class="gallery-img" @click="previewImg = img" />
        </div>
      </el-card>
    </div>

    <el-empty v-if="!product && loaded" description="商品不存在或已下架" />

    <!-- 图片预览 -->
    <el-dialog v-model="previewImg" :show-close="false" class="preview-dialog">
      <img :src="previewImg" style="width: 100%; border-radius: 8px;" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProductDetail } from '../api'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const product = ref(null)
const currentImage = ref('')
const previewImg = ref('')
const quantity = ref(1)
const loaded = ref(false)
const defaultImg = 'https://placekitten.com/400/300'

const categoryMap = {
  cat_food: '🐟 猫粮',
  cat_treat: '🥫 猫条/零食',
  cat_litter: '📦 猫砂',
  cat_toy: '🎾 玩具',
  cat_bed: '🏠 猫窝',
  cat_other: '📦 其他'
}

const categoryLabel = computed(() => categoryMap[product.value?.category] || product.value?.category)

const allImages = computed(() => {
  if (!product.value) return []
  const images = []
  if (product.value.imageUrl) images.push(product.value.imageUrl)
  if (product.value.imageUrls) {
    try {
      const extras = JSON.parse(product.value.imageUrls)
      if (Array.isArray(extras)) {
        extras.forEach(url => { if (url && !images.includes(url)) images.push(url) })
      }
    } catch (e) { /* ignore */ }
  }
  return images
})

const loadProduct = async () => {
  const res = await getProductDetail(route.params.id)
  if (res.data.code === 200 && res.data.data) {
    product.value = res.data.data
    currentImage.value = product.value.imageUrl || defaultImg
  }
  loaded.value = true
}

const handleBuy = () => {
  ElMessage.info('请联系平台客服购买此商品')
}

onMounted(() => loadProduct())
</script>

<style scoped>
.product-detail { min-height: 100vh; background: #f5f5f5; }
.navbar { display: flex; justify-content: space-between; padding: 12px 30px; background: white; box-shadow: 0 2px 12px rgba(0,0,0,0.08); }
.logo { font-size: 20px; font-weight: bold; color: #e6a23c; cursor: pointer; }
.container { max-width: 1200px; margin: 20px auto; padding: 0 20px; }
.img-card, .info-card, .gallery-card { border-radius: 12px; }
.main-img { width: 100%; border-radius: 12px; max-height: 450px; object-fit: contain; background: #fafafa; }
.thumb-list { display: flex; gap: 8px; margin-top: 12px; flex-wrap: wrap; }
.thumb { width: 60px; height: 60px; object-fit: cover; border-radius: 6px; cursor: pointer; border: 2px solid transparent; opacity: 0.7; }
.thumb.active { border-color: #409eff; opacity: 1; }
.info-card h1 { margin: 0 0 8px; }
.brand { color: #999; font-size: 14px; margin: 5px 0; }
.price { font-size: 32px; font-weight: bold; color: #e6a23c; margin: 15px 0; }
.stock-info { margin: 10px 0; }
.info-list { margin: 20px 0; }
.actions { display: flex; align-items: center; gap: 15px; margin-top: 20px; }

.gallery-card { margin-top: 20px; }
.gallery-card h2 { margin: 0 0 15px; }
.gallery-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(150px, 1fr)); gap: 12px; }
.gallery-img { width: 100%; height: 120px; object-fit: cover; border-radius: 8px; cursor: pointer; }
.gallery-img:hover { opacity: 0.8; }
</style>
