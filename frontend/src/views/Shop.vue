<template>
  <div class="shop">
    <div class="navbar">
      <div class="logo">🛒 猫咪用品</div>
      <el-button link @click="$router.push('/')">返回首页</el-button>
    </div>

    <div class="container">
      <el-tabs v-model="activeTab" @tab-change="loadProducts">
        <el-tab-pane label="🐟 猫粮" name="cat_food" />
        <el-tab-pane label="🥫 猫条/零食" name="cat_treat" />
        <el-tab-pane label="📦 猫砂" name="cat_litter" />
        <el-tab-pane label="🎾 玩具" name="cat_toy" />
        <el-tab-pane label="🏠 猫窝" name="cat_bed" />
        <el-tab-pane label="📦 其他" name="cat_other" />
      </el-tabs>

      <div class="product-grid">
        <el-card v-for="p in products" :key="p.id" class="product-card" shadow="hover" @click="goDetail(p.id)">
          <div class="img-wrapper">
            <img :src="p.imageUrl || defaultImg" class="product-img" />
            <el-tag v-if="p.imageUrls" class="multi-tag" type="info" size="small">{{ imageCount(p) }}张图</el-tag>
          </div>
          <div class="info">
            <h3>{{ p.name }}</h3>
            <p class="brand">{{ p.brand }}</p>
            <p class="price">¥{{ p.price }}</p>
            <p class="stock">库存: {{ p.stock }}</p>
          </div>
        </el-card>
      </div>

      <el-empty v-if="products.length === 0" description="暂无商品"></el-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getProductList } from '../api'

const router = useRouter()
const products = ref([])
const activeTab = ref('cat_food')
const defaultImg = 'https://placekitten.com/300/200'

const loadProducts = () => {
  getProductList(activeTab.value).then(res => {
    if (res.data.code === 200) products.value = res.data.data
  })
}

const imageCount = (p) => {
  try { return JSON.parse(p.imageUrls || '[]').length + 1 } catch { return 1 }
}

const goDetail = (id) => router.push(`/product/${id}`)

onMounted(() => loadProducts())
</script>

<style scoped>
.shop { min-height: 100vh; background: #f5f5f5; }
.navbar { display: flex; justify-content: space-between; padding: 12px 30px; background: white; box-shadow: 0 2px 12px rgba(0,0,0,0.08); }
.logo { font-size: 20px; font-weight: bold; color: #e6a23c; }
.container { max-width: 1200px; margin: 20px auto; padding: 0 20px; }
.product-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 20px; margin-top: 20px; }
.product-card { cursor: pointer; border-radius: 12px; overflow: hidden; transition: all 0.3s; }
.product-card:hover { transform: translateY(-5px); }
.img-wrapper { position: relative; }
.product-img { width: 100%; height: 180px; object-fit: cover; }
.multi-tag { position: absolute; top: 8px; right: 8px; }
.info { padding: 15px; }
.info h3 { margin: 0 0 8px; font-size: 16px; }
.brand { color: #999; font-size: 13px; margin: 5px 0; }
.price { font-size: 20px; font-weight: bold; color: #e6a23c; margin: 10px 0 0; }
.stock { color: #999; font-size: 12px; }
</style>
