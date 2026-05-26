import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import CatDetail from '../views/CatDetail.vue'
import MyAdoptions from '../views/MyAdoptions.vue'
import Profile from '../views/Profile.vue'
import Admin from '../views/Admin.vue'
import Market from '../views/Market.vue'
import Shop from '../views/Shop.vue'
import AddCat from '../views/AddCat.vue'

const routes = [
  { path: '/', name: 'Home', component: Home },
  { path: '/login', name: 'Login', component: Login },
  { path: '/register', name: 'Register', component: Register },
  { path: '/cat/:id', name: 'CatDetail', component: CatDetail },
  { path: '/market', name: 'Market', component: Market },
  { path: '/shop', name: 'Shop', component: Shop },
  { path: '/add-cat', name: 'AddCat', component: AddCat, meta: { requiresAuth: true } },
  { path: '/my-cats', name: 'MyCats', component: AddCat, meta: { requiresAuth: true, isMyCats: true } },
  { path: '/my-adoptions', name: 'MyAdoptions', component: MyAdoptions, meta: { requiresAuth: true } },
  { path: '/profile', name: 'Profile', component: Profile, meta: { requiresAuth: true } },
  { path: '/admin', name: 'Admin', component: Admin, meta: { requiresAuth: true, requiresAdmin: true } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  let user = {}
  try {
    user = JSON.parse(localStorage.getItem('user')) || {}
  } catch (e) {
    localStorage.removeItem('user')
  }

  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.meta.requiresAdmin && user.role !== 'admin') {
    next('/')
  } else {
    next()
  }
})

export default router
