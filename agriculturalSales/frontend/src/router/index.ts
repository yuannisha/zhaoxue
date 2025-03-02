// @ts-ignore
import { createRouter, createWebHashHistory, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import ProductList from '../views/ProductList.vue'
import Cart from '../views/Cart.vue'
import ProductManagement from '../views/ProductManagement.vue'
import Profile from '../views/Profile.vue'

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home
    },
    {
      path: '/login',
      name: 'login',
      component: Login
    },
    {
      path: '/register',
      name: 'register',
      component: Register
    },
    {
      path: '/products',
      name: 'products',
      component: ProductList
    },
    {
      path: '/cart',
      name: 'cart',
      component: Cart,
      meta: { requiresAuth: true, role: 'CUSTOMER' }
    },
    {
      path: '/product-management',
      name: 'product-management',
      component: ProductManagement,
      meta: { requiresAuth: true, role: 'SELLER' }
    },
    {
      path: '/profile',
      name: 'profile',
      component: Profile,
      meta: { requiresAuth: true }
    },
    {
      path: '/orders',
      name: 'orders',
      component: () => import('../views/OrderList.vue'),
      meta: { requiresAuth: true, roles: ['CUSTOMER'] }
    },
    {
      path: '/orders/:id',
      name: 'orderDetail',
      component: () => import('../views/OrderDetail.vue'),
      meta: { requiresAuth: true, roles: ['CUSTOMER', 'SELLER'] }
    },
    {
      path: '/seller/orders',
      name: 'sellerOrders',
      component: () => import('../views/SellerOrders.vue'),
      meta: { requiresAuth: true, roles: ['SELLER'] }
    }
  ]
})

router.beforeEach((to, _from, next) => {
  const userStore = useUserStore()
  
  if (to.meta.requiresAuth && !userStore.token) {
    next('/login')
  } else if (to.meta.role && to.meta.role !== userStore.role) {
    next('/')
  } else {
    next()
  }
})

export default router 