<template>
  <div class="cart">
    <h2>我的购物车</h2>
    <el-table 
      :data="cartItems" 
      v-loading="loading"
      @selection-change="handleSelectionChange"
    >
      <el-table-column
        type="selection"
        width="55">
      </el-table-column>
      <el-table-column label="商品图片" width="180">
        <template #default="{ row }">
          <img :src="`http://localhost:8080/api${row.productImage}`" class="product-image">
        </template>
      </el-table-column>
      <el-table-column prop="productName" label="商品名称" />
      <el-table-column prop="productDescription" label="商品描述" />
      <el-table-column prop="productPrice" label="单价" width="120">
        <template #default="{ row }">
          ¥{{ row.productPrice }}
        </template>
      </el-table-column>
      <el-table-column label="数量" width="200">
        <template #default="{ row }">
          <el-input-number 
            v-model="row.quantity" 
            :min="1" 
            @change="(value) => handleQuantityChange(row.id,row.productId, value)"
          />
        </template>
      </el-table-column>
      <el-table-column label="小计" width="120">
        <template #default="{ row }">
          ¥{{ (row.totalPrice).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button type="danger" @click="handleRemove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="cart-footer" v-if="cartItems.length > 0">
      <div class="total">
        已选择 {{ selectedItems.length }} 件商品
        总计: ¥{{ selectedTotal.toFixed(2) }}
      </div>
      <el-button 
        type="primary" 
        @click="beforeCheckout"
        :disabled="selectedItems.length === 0"
      >
        结算选中商品
      </el-button>
    </div>

    <el-dialog
      title="请填写收货地址"
      v-model="dialogVisible"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="收件人姓名" prop="receiverName">
          <el-input v-model="form.receiverName" />
        </el-form-item>
        <el-form-item label="收件人电话" prop="receiverPhone">
          <el-input v-model="form.receiverPhone" />
        </el-form-item>
        <el-form-item label="收件人地址" prop="receiverAddress">
          <el-input type="textarea" v-model="form.receiverAddress" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCheckout" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCartItems, updateCartItem, removeFromCart } from '../api/cart'
import type { CartItem } from '../api/cart'
import { createOrder } from '../api/order'
import type { FormInstance } from 'element-plus'

const cartItems = ref<CartItem[]>([])
const loading = ref(false)
const selectedItems = ref<CartItem[]>([])
const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref<FormInstance>()

const form = ref({
  cartItemIds:undefined as number[] | undefined,
  receiverName: undefined as string | undefined,
  receiverPhone: undefined as string | undefined,
  receiverAddress: undefined as string | undefined,
})

const rules = {
  receiverName: [{ required: true, message: '请输入收件人姓名', trigger: 'blur' }],
  receiverPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  receiverAddress: [{ required: true, message: '请输入收货地址', trigger: 'blur' }],
}

const selectedTotal = computed(() => {
  return selectedItems.value.reduce((sum, item) => {
    return sum + item.totalPrice
  }, 0)
})

const loadCartItems = async () => {
  loading.value = true
  try {
    // @ts-ignore
    cartItems.value = await getCartItems()
    console.log(cartItems)
  } catch (error) {
    ElMessage.error('获取购物车失败')
  } finally {
    loading.value = false
  }
}

const handleQuantityChange = async (id: number, productId: number, quantity: number) => {
  try {
    await updateCartItem(id,productId, quantity)
    ElMessage.success('更新数量成功')
    loadCartItems() // 重新加载购物车
  } catch (error) {
    ElMessage.error('更新数量失败：' + error.response.data.message)
    loadCartItems() // 重新加载购物车
  }
}

const handleRemove = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗？')
    await removeFromCart(id)
    ElMessage.success('删除成功')
    loadCartItems()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSelectionChange = (items: CartItem[]) => {
  selectedItems.value = items
}

const beforeCheckout = async ()=>{
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请至少选择一件商品')
    return
  }

  await ElMessageBox.confirm('确定要结算选中的商品吗？')
    const itemIds = selectedItems.value.map(item => item.id)
    form.value.cartItemIds = itemIds

  dialogVisible.value = true
}

const handleCheckout = async () => {
  

  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true

  try {
    console.log(form.value)
    await createOrder(form.value)
    ElMessage.success('结算成功')
    dialogVisible.value = false
    loadCartItems() // 重新加载购物车
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('结算失败，请稍后重试')
    }
  }finally{
    submitting.value = false
  }
}})
}

onMounted(() => {
  loadCartItems()
})
</script>

<style scoped>
.cart {
  padding: 20px;
}

.product-image {
  width: 100px;
  height: 100px;
  object-fit: cover;
}

.cart-footer {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 20px;
}

.total {
  font-size: 20px;
  color: #f56c6c;
  font-weight: bold;
}
</style> 