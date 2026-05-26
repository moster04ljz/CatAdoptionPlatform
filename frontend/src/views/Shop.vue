<template>
  <div class="shop">
    <div class="navbar">
      <div class="logo">🛒 猫咪商城</div>
      <el-button link @click="$router.push('/')">返回首页</el-button>
    </div>
    
    <div class="container">
      <el-tabs v-model="activeTab" @tab-click="loadProducts">
        <el-tab-pane label="🐟 猫粮" name="cat_food">
          <div class="product-grid">
            <el-card v-for="p in products" :key="p.id" class="product-card" shadow="hover" @click="goDetail(p.id)">
              <img :src="p.imageUrl || defaultImg" class="product-img" />
              <div class="info">
                <h3>{{ p.name }}</h3>
                <p class="brand">{{ p.brand }}</p>
                <p class="price">¥{{ p.price }}</p>
                <p class="stock">库存: {{ p.stock }}</p>
              </div>
            </el-card>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="🥫 猫条/零食" name="cat_treat">
          <div class="product-grid">
            <el-card v-for="p in products" :key="p.id" class="product-card" shadow="hover" @click="goDetail(p.id)">
              <img :src="p.imageUrl || defaultImg" class="product-img" />
              <div class="info">
                <h3>{{ p.name }}</h3>
                <p class="brand">{{ p.brand }}</p>
                <p class="price">¥{{ p.price }}</p>
              </div>
            </el-card>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="📦 猫砂" name="cat_litter">
          <div class="product-grid">
            <el-card v-for="p in products" :key="p.id" class="product-card" shadow="hover" @click="goDetail(p.id)">
              <img :src="p.imageUrl || defaultImg" class="product-img" />
              <div class="info">
                <h3>{{ p.name }}</h3>
                <p class="brand">{{ p.brand }}</p>
                <p class="price">¥{{ p.price }}</p>
              </div>
            </el-card>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="🎾 玩具" name="cat_toy">
          <div class="product-grid">
            <el-card v-for="p in products" :key="p.id" class="product-card" shadow="hover" @click="goDetail(p.id)">
              <img :src="p.imageUrl || defaultImg" class="product-img" />
              <div class="info">
                <h3>{{ p.name }}</h3>
                <p class="brand">{{ p.brand }}</p>
                <p class="price">¥{{ p.price }}</p>
              </div>
            </el-card>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="🏠 猫窝" name="cat_bed">
          <div class="product-grid">
            <el-card v-for="p in products" :key="p.id" class="product-card" shadow="hover" @click="goDetail(p.id)">
              <img :src="p.imageUrl || defaultImg" class="product-img" />
              <div class="info">
                <h3>{{ p.name }}</h3>
                <p class="brand">{{ p.brand }}</p>
                <p class="price">¥{{ p.price }}</p>
              </div>
            </el-card>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="📦 其他" name="cat_other">
          <div class="product-grid">
            <el-card v-for="p in products" :key="p.id" class="product-card" shadow="hover" @click="goDetail(p.id)">
              <img :src="p.imageUrl || defaultImg" class="product-img" />
              <div class="info">
                <h3>{{ p.name }}</h3>
                <p class="brand">{{ p.brand }}</p>
                <p class="price">¥{{ p.price }}</p>
              </div>
            </el-card>
          </div>
        </el-tab-pane>
      </el-tabs>
      
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

const loadProducts = async (tabName = activeTab.value) => {
  const res = await getProductList(tabName)
  if (res.data.code === 200) {
    products.value = res.data.data
  }
}

const goDetail = (id) => {
  // TODO: 商品详情页
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.shop { min-height: 100vh; background: #f5f5f5; }
.navbar { display: flex; justify-content: space-between; padding: 12px 30px; background: white; box-shadow: 0 2px 12px rgba(0,0,0,0.08); }
.logo { font-size: 20px; font-weight: bold; color: #e6a23c; }
.container { max-width: 1200px; margin: 20px auto; padding: 0 20px; }
.product-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 20px; margin-top: 20px; }
.product-card { cursor: pointer; border-radius: 12px; overflow: hidden; transition: all 0.3s; }
.product-card:hover { transform: translateY(-5px); }
.product-img { width: 100%; height: 180px; object-fit: cover; }
.info { padding: 15px; }
.info h3 { margin: 0 0 8px; font-size: 16px; }
.brand { color: #999; font-size: 13px; margin: 5px 0; }
.price { font-size: 20px; font-weight: bold; color: #e6a23c; margin: 10px 0 0; }
.stock { color: #999; font-size: 12px; }
</style>