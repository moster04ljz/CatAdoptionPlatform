import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器：自动附加 JWT Token
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截器：统一处理错误
api.interceptors.response.use(
  response => response,
  error => {
    if (error.response) {
      const { status } = error.response
      if (status === 401) {
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        // 如果不在登录页，跳转到登录页
        if (window.location.hash !== '#/login') {
          window.location.hash = '#/login'
        }
      }
    }
    return Promise.reject(error)
  }
)

// ======== 猫咪相关 ========
export function getCatList() {
  return api.get('/cat/list')
}

export function getCatAdminAll() {
  return api.get('/cat/admin/all')
}

export function getCatHot() {
  return api.get('/cat/hot')
}

export function getCatAdoption(page = 1, size = 20) {
  return api.get('/cat/adoption', { params: { page, size } })
}

export function getCatMarket(page = 1, size = 20) {
  return api.get('/cat/market', { params: { page, size } })
}

export function getCatById(id) {
  return api.get(`/cat/${id}`)
}

export function getMyCats(userId) {
  return api.get('/cat/my', { params: { userId } })
}

export function getPendingCats() {
  return api.get('/cat/pending')
}

export function addCat(data) {
  return api.post('/cat/add', data)
}

export function updateCat(data) {
  return api.put('/cat/update', data)
}

export function approveCat(id, approved) {
  return api.put('/cat/approve', { id, approved })
}

export function deleteCat(id) {
  return api.delete(`/cat/${id}`)
}

// ======== 评论相关 ========
export function getComments(catId) {
  return api.get(`/comment/cat/${catId}`)
}

export function addComment(data) {
  return api.post('/comment/add', data)
}

export function deleteComment(id) {
  return api.delete(`/comment/${id}`)
}

// ======== 用户相关 ========
export function login(data) {
  return api.post('/user/login', data)
}

export function register(data) {
  return api.post('/user/register', data)
}

export function getUserInfo(id) {
  return api.get(`/user/${id}`)
}

export function getCurrentUser() {
  return api.get('/user/current')
}

export function updateUser(data) {
  return api.put('/user/update', data)
}

// ======== 领养相关 ========
export function getAdoptionList() {
  return api.get('/adoption/list')
}

export function getAdoptionsByCat(catId) {
  return api.get(`/adoption/cat/${catId}`)
}

export function getAdoptionsByUser(userId) {
  return api.get(`/adoption/user/${userId}`)
}

export function applyAdoption(data) {
  return api.post('/adoption/apply', data)
}

export function approveAdoption(id, approved) {
  return api.put('/adoption/approve', { id, approved })
}

// ======== 猫咪用品 ========
export function getProductList(category) {
  return api.get('/product/list', { params: { category } })
}

export function getProductById(id) {
  return api.get(`/product/${id}`)
}

// ======== 轮播图 ========
export function getCarousels() {
  return api.get('/carousel/active')
}

// ======== 文件上传 ========
export function uploadImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return api.post('/upload/image', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
