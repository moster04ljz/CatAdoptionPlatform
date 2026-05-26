<template>
  <div class="market">
    <div class="navbar">
      <div class="logo">🏠 猫咪领养</div>
      <el-button link @click="$router.push('/')">返回首页</el-button>
    </div>
    
    <div class="container">
      <el-tabs v-model="activeTab" @tab-click="loadCats">
        <el-tab-pane label="🐱 领养专区" name="adoption">
          <div class="cat-grid">
            <el-card v-for="cat in cats" :key="cat.id" class="cat-card" shadow="hover" @click="goDetail(cat.id)">
              <div class="cat-img-wrapper">
                <img :src="cat.imageUrl || defaultImage" class="cat-image" />
                <el-tag class="status-tag" type="success">可领养</el-tag>
              </div>
              <div class="cat-info">
                <h3>{{ cat.name }}</h3>
                <p class="age">{{ cat.age }}岁</p>
                <p class="breed">{{ cat.breed }}</p>
                <p class="price free">免费领养</p>
              </div>
            </el-card>
          </div>
        </el-tab-pane>
      </el-tabs>
      
      <el-empty v-if="cats.length === 0" description="暂无待领养的猫咪"></el-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCatAdoption } from '../api'

const router = useRouter()
const cats = ref([])
const activeTab = ref('adoption')
const defaultImage = 'https://placekitten.com/300/200'

const loadCats = async () => {
  const res = await getCatAdoption()
  if (res.data.code === 200) {
    cats.value = res.data.data
  }
}

const goDetail = (id) => router.push(`/cat/${id}`)

onMounted(() => {
  loadCats()
})
</script>

<style scoped>
.market { min-height: 100vh; background: #f5f5f5; }
.navbar { display: flex; justify-content: space-between; padding: 12px 30px; background: white; box-shadow: 0 2px 12px rgba(0,0,0,0.08); }
.logo { font-size: 20px; font-weight: bold; color: #67c23a; }
.container { max-width: 1200px; margin: 20px auto; padding: 0 20px; }
.cat-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); gap: 20px; margin-top: 20px; }
.cat-card { cursor: pointer; transition: all 0.3s; border-radius: 12px; overflow: hidden; }
.cat-card:hover { transform: translateY(-5px); }
.cat-img-wrapper { position: relative; height: 180px; }
.cat-image { width: 100%; height: 100%; object-fit: cover; }
.status-tag { position: absolute; top: 10px; right: 10px; }
.cat-info { padding: 15px; }
.cat-info h3 { margin: 0 0 8px; font-size: 16px; }
.age { color: #666; font-size: 13px; margin: 5px 0; }
.breed { color: #999; font-size: 12px; }
.price.free { color: #67c23a; font-weight: bold; margin: 10px 0 0; }
</style>