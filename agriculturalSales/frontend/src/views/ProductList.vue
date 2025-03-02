<template>
  <div class="product-list">
    <el-row :gutter="20">
      <el-col :span="24">
        <div class="toolbar">
          <el-form :inline="true" :model="searchForm">
            <el-input
              v-model="searchForm.keyword"
              placeholder="搜索商品"
              class="search-input"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-form-item>
              <el-select v-model="searchForm.type" placeholder="商品类型" clearable style="width:300px; height:40px;">
                <el-option
                  v-for="(label, type) in ProductTypeLabels"
                  :key="type"
                  :label="label"
                  :value="type"
                />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-input-number
                v-model="searchForm.minPrice"
                placeholder="最低价格"
                :min="0"
                :precision="2"
              />
            </el-form-item>
            <el-form-item>
              <el-input-number
                v-model="searchForm.maxPrice"
                placeholder="最高价格"
                :min="0"
                :precision="2"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button @click="resetSearch">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-col>
    </el-row>
    
    <el-row :gutter="20">
      <el-col :span="6" v-for="product in products" :key="product.id">
        <!-- 如果商品库存为0，则不显示商品 -->
        <el-card class="product-card" v-if="product.stock > 0">
          <img :src="getImageUrl(product.image)" class="product-image">
          <h3>{{ product.name }}</h3>
          <div class="type">{{ ProductTypeLabels[product.type] }}</div>
          <p class="description">{{ product.description }}</p>
          <div class="price">¥{{ product.price }}</div>
          <div class="actions" v-if="userStore.role === 'CUSTOMER'">
            <el-input-number 
              v-model="product.quantity" 
              :min="1" 
              :max="product.stock"
              size="small"
            />
            <el-button type="primary" @click="handleAddToCart(product)">加入购物车</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :total="total"
      :page-sizes="[12, 24, 36, 48]"
      layout="total, sizes, prev, pager, next"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { searchProducts, ProductType, ProductTypeLabels } from '../api/product'
import { addToCart } from '../api/cart'
import { useUserStore } from '../stores/user'
import type { Product } from '../api/product'
import { getImageUrl } from '../utils/image'
import type { AxiosResponse } from 'axios'

const userStore = useUserStore()
const products = ref<(Product & { quantity: number })[]>([])
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

const searchForm = ref({
  keyword: undefined as string | undefined,
  type: undefined as ProductType | undefined,
  minPrice: undefined as number | undefined,
  maxPrice: undefined as number | undefined
})

const loadProducts = async () => {
  try {
    const data: AxiosResponse = await searchProducts({
        ...searchForm.value,
        page: currentPage.value - 1,
        size: pageSize.value,
        sort: 'createdAt,desc',
    })
    // @ts-ignore: 忽略这行的类型错误
    products.value = data.content.map((product: any) => ({ ...product, quantity: 1 }))
    // @ts-ignore: 忽略这行的类型错误
    total.value = data.totalElements
  } catch (error) {
    ElMessage.error('获取商品列表失败')
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadProducts()
}

const resetSearch = () => {
  searchForm.value = {
    keyword: undefined as string | undefined,
  type: undefined as ProductType | undefined,
  minPrice: undefined as number | undefined,
  maxPrice: undefined as number | undefined
  }
  handleSearch()
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  loadProducts()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  loadProducts()
}

const handleAddToCart = async (product: Product & { quantity: number }) => {
  try {
    await addToCart(product.id, product.quantity)
    ElMessage.success('添加到购物车成功')
  } catch (error) {
    console.log(error)
    // @ts-ignore: 忽略这行的类型错误
    ElMessage.error('添加到购物车失败：' + error.response.data.message)
  }
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.product-list {
  padding: 20px;
}

.toolbar {
  margin-bottom: 20px;
}

.search-input {
  width: 300px;
}

.product-card {
  margin-bottom: 20px;
}

.product-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.description {
  color: #666;
  margin: 10px 0;
}

.price {
  color: #f56c6c;
  font-size: 20px;
  font-weight: bold;
  margin: 10px 0;
}

.actions {
  display: flex;
  gap: 10px;
  align-items: center;
}
</style> 