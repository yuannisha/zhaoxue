<template>
  <div class="home">
    <el-row :gutter="20">
      <el-col :span="24">
        <div class="welcome-section">
          <h1>欢迎来到农产品销售系统</h1>
          <p>这里有最新鲜的农产品，直接从农场到餐桌</p>
        </div>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="8" v-for="feature in features" :key="feature.title">
        <el-card class="feature-card">
          <el-icon :size="40" class="feature-icon">
            <component :is="feature.icon" />
          </el-icon>
          <h3>{{ feature.title }}</h3>
          <p>{{ feature.description }}</p>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ShoppingCart, Box, Van } from '@element-plus/icons-vue'

const features = [
  {
    icon: ShoppingCart,
    title: '便捷购物',
    description: '轻松浏览和购买各类新鲜农产品'
  },
  {
    icon: Box,
    title: '品质保证',
    description: '严格把控产品质量，确保食品安全'
  },
  {
    icon: Van,
    title: '快速配送',
    description: '专业物流配送，保证新鲜送达'
  }
]
</script>

<style scoped>
.home {
  padding: 20px;
}

.welcome-section {
  text-align: center;
  margin-bottom: 40px;
  padding: 40px 0;
  background-color: #f0f9eb;
  border-radius: 8px;
}

.welcome-section h1 {
  color: #67c23a;
  margin-bottom: 20px;
}

.feature-card {
  text-align: center;
  padding: 20px;
  margin-bottom: 20px;
  transition: transform 0.3s;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-icon {
  color: #409EFF;
  margin-bottom: 20px;
}

.feature-card h3 {
  margin: 10px 0;
  color: #303133;
}

.feature-card p {
  color: #606266;
}
</style> 