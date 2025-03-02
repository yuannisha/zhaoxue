<template>
  <div class="seller-orders">
    <h2>商家订单管理</h2>
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
      <el-table-column label="收货信息" width="200">
        <template #default="{ row }">
          <div>{{ row.receiverName }}</div>
          <div>{{ row.receiverPhone }}</div>
          <div class="address-text">{{ row.receiverAddress }}</div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button 
            v-if="row.status === 'PENDING_SHIPMENT'"
            type="primary" 
            size="small" 
            @click="handleShip(row)"
          >
            发货
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

    <!-- 发货对话框 -->
    <el-dialog
      v-model="shipDialogVisible"
      title="填写发货信息"
      width="500px"
    >
      <el-form :model="shipForm" label-width="100px">
        <el-form-item label="快递公司">
          <el-input v-model="shipForm.shippingCompany" />
        </el-form-item>
        <el-form-item label="快递单号">
          <el-input v-model="shipForm.trackingNumber" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="shipDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmShip">
            确认发货
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getSellerOrders, shipOrder } from '../api/order'
import type { Order } from '../api/order'

const router = useRouter()
const orders = ref<Order[]>([])
const loading = ref(false)
const shipDialogVisible = ref(false)
const currentOrder = ref<Order | null>(null)
const shipForm = ref({
  shippingCompany: '',
  trackingNumber: ''
})

const loadOrders = async () => {
  loading.value = true
  try {
    // @ts-ignore
    orders.value = await getSellerOrders()
  } catch (error) {
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

const handleShip = (order: Order) => {
  currentOrder.value = order
  shipForm.value = {
    shippingCompany: '',
    trackingNumber: ''
  }
  shipDialogVisible.value = true
}

const confirmShip = async () => {
  if (!currentOrder.value) return
  if (!shipForm.value.shippingCompany || !shipForm.value.trackingNumber) {
    ElMessage.warning('请填写完整的发货信息')
    return
  }

  try {
    await shipOrder(
      currentOrder.value.id,
      shipForm.value.trackingNumber,
      shipForm.value.shippingCompany
    )
    ElMessage.success('发货成功')
    shipDialogVisible.value = false
    loadOrders()
  } catch (error) {
    ElMessage.error('发货失败')
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
.seller-orders {
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

.address-text {
  white-space: normal;
  word-break: break-all;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 