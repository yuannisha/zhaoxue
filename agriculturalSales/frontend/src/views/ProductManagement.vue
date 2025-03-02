<template>
  <div class="product-management">
    <div class="toolbar">
      <el-button type="primary" @click="handleAdd">添加商品</el-button>
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
              <el-select v-model="searchForm.type" placeholder="商品类型" clearable style="width:300px; height:40px; line-height: 40px;" >
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

    <el-table :data="products" v-loading="loading">
      <el-table-column label="商品图片" width="180">
        <template #default="{ row }">
          <img :src="getImageUrl(row.image)" class="product-image">
        </template>
      </el-table-column>
      <el-table-column prop="name" label="商品名称" />
      <el-table-column prop="type" label="商品类型" width="120">
        <template #default="{ row }">
          {{ ProductTypeLabels[row.type as keyof typeof ProductTypeLabels] }}
        </template>
      </el-table-column>
      <el-table-column prop="price" label="价格" width="120">
        <template #default="{ row }">
          ¥{{ row.price }}
        </template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="120" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :total="totalDataCount"
      :page-sizes="[12, 24, 36, 48]"
      layout="total, sizes, prev, pager, next"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    ></el-pagination>

    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input type="textarea" v-model="form.description" />
        </el-form-item>
        <el-form-item label="商品类型" prop="type">
              <el-select v-model="form.type" placeholder="商品类型" clearable style="width:300px; height:40px; line-height: 40px;" >
                <el-option
                  v-for="(label, type) in ProductTypeLabels"
                  :key="type"
                  :label="label"
                  :value="type"
                />
              </el-select>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :precision="2" :step="0.1" :min="0" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" />
        </el-form-item>
        <el-form-item label="商品图片" prop="image" class="image-upload-item">
          <ImageUpload v-model="form.image" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSellerProducts, createProduct, updateProduct, deleteProduct } from '../api/product'
import { ProductType, ProductTypeLabels } from '../api/product'
import type { Product } from '../api/product'
import type { FormInstance } from 'element-plus'
import ImageUpload from '../components/ImageUpload.vue'
import { getImageUrl } from '../utils/image'

const products = ref<Product[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref<FormInstance>()
const isEdit = ref(false)

const currentPage = ref(1)
const pageSize = ref(12)
const totalDataCount = ref(0)

const form = ref({
  id: undefined as number | undefined,
  name: '',
  description: '',
  type: '',
  price: 0,
  stock: 0,
  image: ''
})

const searchForm = ref({
  keyword: undefined as string | undefined,
  type: undefined as ProductType | undefined,
  minPrice: undefined as number | undefined,
  maxPrice: undefined as number | undefined
})

const rules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  description: [{ required: true, message: '请输入商品描述', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }],
  image: [{ required: true, message: '请上传商品图片', trigger: 'change' }]
}

const dialogTitle = computed(() => isEdit.value ? '编辑商品' : '添加商品')

const loadProducts = async () => {
  loading.value = true
  try {
    const response = await getSellerProducts({...searchForm.value,
      page: currentPage.value - 1,
      size: pageSize.value,
      sort: 'createdAt,desc'})
      console.log(response)
      // @ts-ignore
    products.value = response.content
    // @ts-ignore
    totalDataCount.value = response.totalElements
  } catch (error) {
    ElMessage.error('获取商品列表失败')
    console.log(error)
  } finally {
    loading.value = false
  }
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

const handleSearch = () => {
  currentPage.value = 1
  loadProducts()
}

const handleAdd = () => {
  isEdit.value = false
  // @ts-ignore
  form.value = {
    id: undefined,
    name: '',
    description: '',
    price: 0,
    stock: 0,
    image: ''
  }
  dialogVisible.value = true
}

const handleEdit = (row: Product) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗？')
    await deleteProduct(id)
    ElMessage.success('删除成功')
    loadProducts()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async () => {
    console.log(form.value)
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        if (isEdit.value) {
          // @ts-ignore
          await updateProduct(form.value.id!, form.value)
          ElMessage.success('更新成功')
        } else {
          console.log("添加商品到这里了")
          // @ts-ignore
          await createProduct(form.value)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        loadProducts()
      } catch (error) {
        ElMessage.error(isEdit.value ? '更新失败' : '添加失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.product-management {
  padding: 20px;
}

.toolbar {
  margin-bottom: 20px;
}

.search-input {
  width: 300px;
}

.product-image {
  width: 100px;
  height: 100px;
  object-fit: cover;
}

.image-upload-item {
  width: 100%;
  max-width: 400px;
}
</style> 