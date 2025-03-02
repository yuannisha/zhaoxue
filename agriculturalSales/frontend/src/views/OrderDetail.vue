<template>
  <div class="order-detail" v-loading="loading">
    <h2>订单详情</h2>
    <el-descriptions v-if="order" :column="2" border>
      <el-descriptions-item label="订单编号">{{ order.orderNumber }}</el-descriptions-item>
      <el-descriptions-item label="订单状态">{{ order.statusDescription }}</el-descriptions-item>
      <el-descriptions-item label="收货人">{{ order.receiverName }}</el-descriptions-item>
      <el-descriptions-item label="联系电话">{{ order.receiverPhone }}</el-descriptions-item>
      <el-descriptions-item label="收货地址" :span="2">{{ order.receiverAddress }}</el-descriptions-item>
      <template v-if="order.trackingNumber">
        <el-descriptions-item label="快递公司">{{ order.shippingCompany }}</el-descriptions-item>
        <el-descriptions-item label="快递单号">{{ order.trackingNumber }}</el-descriptions-item>
      </template>
      <el-descriptions-item label="创建时间">{{ formatTime(order.createTime) }}</el-descriptions-item>
      <el-descriptions-item label="总金额">¥{{ order.totalAmount }}</el-descriptions-item>
    </el-descriptions>

    <div class="order-items" v-if="order">
      <h3>商品信息</h3>
      <el-table :data="order.items">
        <el-table-column label="商品图片" width="100">
          <template #default="{ row }">
            <img :src="`http://localhost:8080/api${row.productImage}`" class="product-image">
          </template>
        </el-table-column>
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="price" label="单价">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="100" />
        <el-table-column prop="totalPrice" label="小计">
          <template #default="{ row }">¥{{ row.totalPrice }}</template>
        </el-table-column>
      </el-table>
    </div>

    <div class="order-actions" v-if="order">
      <!-- 如果订单状态为待付款，则显示付款按钮，如果userstore.role为'CUSTOMER'，则显示付款按钮 -->
      <el-button 
        v-if="order.status === 'PENDING_PAYMENT' && userStore.role === 'CUSTOMER'"
        type="primary" 
        @click="handlePay"
      >
        付款
      </el-button>
      <!-- 如果订单状态为已发货，则显示确认收货按钮，如果userstore.role为'CUSTOMER'，则显示确认收货按钮 -->
      <el-button 
        v-if="order.status === 'SHIPPED' && userStore.role === 'CUSTOMER'"
        type="success" 
        @click="handleConfirm"
      >
        确认收货
      </el-button>
      <!-- 如果订单状态为待付款，则显示取消订单按钮，如果userstore.role为'CUSTOMER'，则显示取消订单按钮 -->
      <el-button 
        v-if="order.status === 'PENDING_PAYMENT' && userStore.role === 'CUSTOMER'"
        type="danger" 
        @click="handleCancel"
      >
        取消订单
      </el-button>
      <!-- 显示返回按钮 -->
      <el-button @click="router.back()">返回</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrder, payOrder, confirmReceipt, cancelOrder } from '../api/order'
import type { Order } from '../api/order'
//导入userstore
import { useUserStore } from '../stores/user'

//当用户登录时，获取用户信息
const userStore = ref(useUserStore())

const route = useRoute()
const router = useRouter()
const order = ref<Order | null>(null)
const loading = ref(false)

const loadOrder = async () => {
  const orderId = Number(route.params.id)
  if (!orderId) return

  loading.value = true
  try {
    // @ts-ignore
    order.value = await getOrder(orderId)
  } catch (error) {
    ElMessage.error('获取订单详情失败')
  } finally {
    loading.value = false
  }
}

const handlePay = async () => {
  if (!order.value) return
  try {
    await ElMessageBox.confirm('确定要支付该订单吗？')
    await payOrder(order.value.id)
    ElMessage.success('支付成功')
    loadOrder()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('支付失败')
    }
  }
}

const handleConfirm = async () => {
  if (!order.value) return
  try {
    await ElMessageBox.confirm('确定要确认收货吗？')
    await confirmReceipt(order.value.id)
    ElMessage.success('确认收货成功')
    loadOrder()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('确认收货失败')
    }
  }
}

const handleCancel = async () => {
  if (!order.value) return
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？')
    await cancelOrder(order.value.id)
    ElMessage.success('取消订单成功')
    loadOrder()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消订单失败')
    }
  }
}

const formatTime = (time: string) => {
  return new Date(time).toLocaleString()
}

onMounted(() => {
  loadOrder()
})
</script>

<style scoped>
.order-detail {
  padding: 20px;
}

.order-items {
  margin-top: 20px;
}

.product-image {
  width: 50px;
  height: 50px;
  object-fit: cover;
}

.order-actions {
  margin-top: 20px;
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}
</style> 