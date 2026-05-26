<template>
  <div class="home">
    <!-- 顶部导航 -->
    <div class="navbar">
      <div class="logo">🐱 流浪猫领养平台</div>
      <div class="nav-links">
        <el-button link @click="$router.push('/')">首页</el-button>
        <el-button link @click="$router.push('/shop')">猫咪用品</el-button>
        <template v-if="user.id">
          <el-button link @click="$router.push('/add-cat')">发布猫咪</el-button>
          <el-button link @click="$router.push('/my-adoptions')">我的申请</el-button>
          <el-button link @click="$router.push('/profile')">个人中心</el-button>
          <el-button v-if="user.role === 'admin'" type="warning" link @click="$router.push('/admin')">管理后台</el-button>
          <el-dropdown @command="handleCommand">
            <span class="user-dropdown">
              <el-avatar :size="28">{{ user.nickname?.[0] }}</el-avatar>
              <span>{{ user.nickname }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button type="primary" @click="$router.push('/login')">登录</el-button>
          <el-button @click="$router.push('/register')">注册</el-button>
        </template>
      </div>
    </div>
    
    <!-- 轮播图 -->
    <div class="carousel-container">
      <el-carousel height="400px" :interval="5000" indicator-position="outside">
        <el-carousel-item v-for="(item, idx) in carousels" :key="idx">
          <img :src="item.imageUrl || defaultCarousel" class="carousel-img" />
          <div class="carousel-caption">
            <h2>{{ item.title || '让爱流动 · 领养代替购买' }}</h2>
            <p>每一个生命都值得被温柔以待</p>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>
    
    <!-- 热门猫咪 -->
    <div class="section" v-if="hotCats.length > 0">
      <h2 class="section-title">🌟 热门猫咪</h2>
      <div class="cat-grid">
        <el-card v-for="cat in hotCats" :key="cat.id" class="cat-card" shadow="hover" @click="goDetail(cat.id)">
          <div class="cat-img-wrapper">
            <img :src="cat.imageUrl" class="cat-image" />
            <el-tag class="hot-tag" type="danger">热门</el-tag>
          </div>
          <div class="cat-info">
            <h3>{{ cat.name }}</h3>
            <p class="price" v-if="cat.price > 0">¥{{ cat.price }}</p>
            <p class="price free" v-else>免费领养</p>
          </div>
        </el-card>
      </div>
    </div>
    
    <!-- 领养专区 -->
    <div class="section">
      <h2 class="section-title">🏠 领养专区</h2>
      <div class="filter-bar">
        <el-input v-model="search" placeholder="搜索..." clearable @keyup.enter="loadCats" style="width: 200px">
          <template #append><el-button :icon="Search" @click="loadCats" /></template>
        </el-input>
        <el-button type="primary" @click="$router.push('/market')">更多 >></el-button>
      </div>
      <div class="cat-grid">
        <el-card v-for="cat in adoptionCats" :key="cat.id" class="cat-card" shadow="hover" @click="goDetail(cat.id)">
          <div class="cat-img-wrapper">
            <img :src="cat.imageUrl || defaultImage" class="cat-image" />
            <el-tag class="status-tag" type="success">可领养</el-tag>
          </div>
          <div class="cat-info">
            <h3>{{ cat.name }}</h3>
            <p>{{ cat.breed }}</p>
            <div class="tags">
              <el-tag size="small">{{ cat.age }}岁</el-tag>
              <el-tag size="small" :type="cat.gender === 'Male' ? 'blue' : 'pink'">{{ cat.gender }}</el-tag>
              <el-tag size="small">{{ cat.location }}</el-tag>
            </div>
            <p class="desc">{{ cat.description }}</p>
          </div>
        </el-card>
      </div>
    </div>
    
    <!-- 商城专区 -->
    <div class="section">
      <h2 class="section-title">🛒 猫咪商城</h2>
      <div class="cat-grid">
        <el-card v-for="cat in marketCats" :key="cat.id" class="cat-card" shadow="hover" @click="goDetail(cat.id)">
          <div class="cat-img-wrapper">
            <img :src="cat.imageUrl" class="cat-image" />
            <el-tag class="status-tag" type="warning">出售</el-tag>
          </div>
          <div class="cat-info">
            <h3>{{ cat.name }}</h3>
            <p>{{ cat.breed }}</p>
            <p class="price">¥{{ cat.price }}</p>
          </div>
        </el-card>
      </div>
    </div>
    
    <el-empty v-if="adoptionCats.length === 0" description="暂无待领养的猫咪"></el-empty>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCatList, getCarousels } from '../api'
import { Search } from '@element-plus/icons-vue'

const router = useRouter()
const hotCats = ref([])
const adoptionCats = ref([])
const marketCats = ref([])
const carousels = ref([])
const search = ref('')
const user = ref({})
const defaultImage = 'https://placekitten.com/300/200'
const defaultCarousel = 'https://placekitten.com/1200/400'

const loadCats = async () => {
  const res = await getCatList()
  if (res.data.code === 200) {
    const allCats = res.data.data
    hotCats.value = allCats.filter(c => c.isHot && c.status === 'available').slice(0, 4)
    adoptionCats.value = allCats.filter(c => c.category === 'adoption' && c.status === 'available').slice(0, 8)
    marketCats.value = allCats.filter(c => c.category === 'market' && c.status === 'available').slice(0, 4)
  }
}

const loadCarousels = async () => {
  try {
    const res = await getCarousels()
    if (res.data.code === 200 && res.data.data.length > 0) {
      carousels.value = res.data.data
    }
  } catch (e) {
    // fallback to default
  }
}

const goDetail = (id) => router.push(`/cat/${id}`)

const handleCommand = (cmd) => {
  if (cmd === 'profile') router.push('/profile')
  else if (cmd === 'logout') {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    user.value = {}
    router.push('/')
  }
}

onMounted(() => {
  try { user.value = JSON.parse(localStorage.getItem('user')) || {} } catch (e) { user.value = {} }
  loadCats()
  loadCarousels()
})
</script>

<style scoped>
.home { min-height: 100vh; background: #f5f5f5; }

.navbar {
  display: flex; justify-content: space-between; align-items: center;
  padding: 12px 30px; background: white; box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  position: sticky; top: 0; z-index: 100;
}
.logo { font-size: 20px; font-weight: bold; color: #409eff; }
.nav-links { display: flex; align-items: center; gap: 10px; }
.user-dropdown { display: flex; align-items: center; gap: 8px; cursor: pointer; }

.carousel-container { max-width: 1200px; margin: 0 auto; }
.carousel-img { width: 100%; height: 100%; object-fit: cover; }
.carousel-caption {
  position: absolute; bottom: 60px; left: 60px; color: white; text-shadow: 2px 2px 4px rgba(0,0,0,0.5);
}
.carousel-caption h2 { font-size: 36px; margin: 0 0 10px; }
.carousel-caption p { font-size: 18px; margin: 0; }

.section { max-width: 1200px; margin: 40px auto; padding: 0 20px; }
.section-title { font-size: 24px; margin-bottom: 20px; color: #333; border-left: 4px solid #409eff; padding-left: 15px; }
.filter-bar { display: flex; justify-content: space-between; margin-bottom: 20px; }

.cat-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); gap: 20px; }
.cat-card { cursor: pointer; transition: all 0.3s; border-radius: 12px; overflow: hidden; }
.cat-card:hover { transform: translateY(-5px); box-shadow: 0 8px 25px rgba(0,0,0,0.15); }
.cat-img-wrapper { position: relative; height: 180px; }
.cat-image { width: 100%; height: 100%; object-fit: cover; }
.hot-tag, .status-tag { position: absolute; top: 10px; right: 10px; }

.cat-info { padding: 15px; }
.cat-info h3 { margin: 0 0 8px; font-size: 18px; }
.cat-info p { color: #666; margin: 5px 0; }
.tags { display: flex; gap: 5px; margin-bottom: 8px; }
.desc { font-size: 13px; color: #999; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.price { font-size: 18px; font-weight: bold; color: #e6a23c; }
.price.free { color: #67c23a; }
</style>