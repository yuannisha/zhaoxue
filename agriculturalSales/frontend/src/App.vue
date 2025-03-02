<script setup lang="ts">
import { useUserStore } from './stores/user'
import { useRouter } from 'vue-router'
import { ArrowDown } from '@element-plus/icons-vue'

const userStore = useUserStore()
const router = useRouter()

const handleCommand = (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'logout':
      userStore.clearUserInfo()
      router.push('/login')
      break
  }
}
</script>

<template>
  <el-container>
    <el-header>
      <nav class="nav-container">
        <div class="logo">
          <router-link to="/">农产品销售系统</router-link>
        </div>
        <div class="nav-links">
          <router-link to="/">首页</router-link>
          <router-link to="/products">商品列表</router-link>
          <template v-if="!userStore.token">
            <router-link to="/login">登录</router-link>
            <router-link to="/register">注册</router-link>
          </template>
          <template v-else>
            <router-link v-if="userStore.role === 'CUSTOMER'" to="/cart">购物车</router-link>
            <router-link v-if="userStore.role === 'CUSTOMER'" to="/orders">我的订单</router-link>
            <router-link v-if="userStore.role === 'SELLER'" to="/product-management">商品管理</router-link>
            <router-link v-if="userStore.role === 'SELLER'" to="/seller/orders">订单管理</router-link>
            <el-dropdown @command="handleCommand">
              <span class="el-dropdown-link">
                {{ userStore.username }}
                <el-icon class="el-icon--right"><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </div>
      </nav>
    </el-header>
    <el-main>
      <router-view></router-view>
    </el-main>
  </el-container>
</template>


<style scoped>
.nav-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  padding: 0 20px;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.logo {
  font-size: 20px;
  font-weight: bold;
}

.logo a {
  color: #409EFF;
  text-decoration: none;
}

.nav-links {
  display: flex;
  gap: 20px;
  align-items: center;
}

.nav-links a {
  color: #606266;
  text-decoration: none;
  padding: 5px 10px;
  border-radius: 4px;
  transition: all 0.3s;
}

.nav-links a:hover {
  color: #409EFF;
  background-color: #ecf5ff;
}

.el-dropdown-link {
  cursor: pointer;
  color: #409EFF;
  display: flex;
  align-items: center;
}
</style>
