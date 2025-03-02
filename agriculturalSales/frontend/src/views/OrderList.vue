<template>
  <div class="order-list">
    <h2>我的订单</h2>
    <el-table :data="orders" v-loading="loading">
      <el-table-column prop="orderNumber" label="订单编号" width="180" />
      <el-table-column label="商品信息">
        <template #default="{ row }">
          <div v-for="item in row.items" :key="item.id" class="order-item">
            <img :src="`http://localhost:8080/api${item.productImage}`" class="product-image">
            <div class="product-info">
              <div>{{ item.productName }}</div>
              <div>数量: {{ item.quantity }}</div>
              <div>单价: ¥{{ item.price }}</div>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="totalAmount" label="总金额" width="120">
        <template #default="{ row }">
          ¥{{ row.totalAmount }}
        </template>
      </el-table-column>
      <el-table-column prop="statusDescription" label="状态" width="120" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button 
            v-if="row.status === 'PENDING_PAYMENT'"
            type="primary" 
            size="small" 
            @click="handlePay(row)"
          >
            付款
          </el-button>
          <el-button 
            v-if="row.status === 'SHIPPED'"
            type="success" 
            size="small" 
            @click="handleConfirm(row)"
          >
            确认收货
          </el-button>
          <el-button 
            v-if="row.status === 'PENDING_PAYMENT'"
            type="danger" 
            size="small" 
            @click="handleCancel(row)"
          >
            取消订单
          </el-button>
          <el-button
            type="info"
            size="small"
            @click="handleViewDetails(row)"
          >
            查看详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { getUserOrders, payOrder, confirmReceipt, cancelOrder } from '../api/order'
import type { Order } from '../api/order'

const router = useRouter()
const orders = ref<Order[]>([])
const loading = ref(false)

const loadOrders = async () => {
  loading.value = true
  try {
    console.log(orders.value)
    // @ts-ignore
    orders.value = await getUserOrders()
  } catch (error) {
    ElMessage.error('获取订单列表失败：' + error.response.data.message)
  } finally {
    loading.value = false
  }
}

const handlePay = async (order: Order) => {
  try {
    await ElMessageBox.confirm('确定要支付该订单吗？')
    await payOrder(order.id)
    ElMessage.success('支付成功')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('支付失败')
    }
  }
}

const handleConfirm = async (order: Order) => {
  try {
    await ElMessageBox.confirm('确定要确认收货吗？')
    await confirmReceipt(order.id)
    ElMessage.success('确认收货成功')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('确认收货失败')
    }
  }
}

const handleCancel = async (order: Order) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？')
    await cancelOrder(order.id)
    ElMessage.success('取消订单成功')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消订单失败')
    }
  }
}

const handleViewDetails = (order: Order) => {
  router.push(`/orders/${order.id}`)
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.order-list {
  padding: 20px;
}

.order-item {
  display: flex;
  align-items: center;
  margin: 10px 0;
}

.product-image {
  width: 50px;
  height: 50px;
  object-fit: cover;
  margin-right: 10px;
}

.product-info {
  font-size: 14px;
}
</style> 