import request from '../utils/request'
import type { OrderStatus } from '../types/order'

export interface OrderRequest {
  cartItemIds: number[]
  receiverName: string
  receiverPhone: string
  receiverAddress: string
}

export interface OrderItem {
  id: number
  productId: number
  productName: string
  productImage: string
  quantity: number
  price: number
  totalPrice: number
}

export interface Order {
  id: number
  orderNumber: string
  receiverName: string
  receiverPhone: string
  receiverAddress: string
  totalAmount: number
  status: OrderStatus
  statusDescription: string
  trackingNumber: string
  shippingCompany: string
  createTime: string
  payTime: string
  shipTime: string
  completeTime: string
  items: OrderItem[]
}

export const createOrder = (data: OrderRequest) => {
  return request<Order>({
    url: '/orders/customer/create',
    method: 'post',
    data
  })
}

export const getUserOrders = () => {
  return request<Order[]>({
    url: '/orders/customer/orders',
    method: 'get'
  }).then(response => response)
}

export const getSellerOrders = () => {
  return request<Order[]>({
    url: '/orders/seller/orders',
    method: 'get'
  }).then(response => response)
}

export const getOrder = (orderId: number) => {
  return request<Order>({
    url: `/orders/getOrder/${orderId}`,
    method: 'get'
  }).then(response => response)
}

export const payOrder = (orderId: number) => {
  return request({
    url: `/orders/customer/${orderId}/pay`,
    method: 'post'
  })
}

export const shipOrder = (orderId: number, trackingNumber: string, shippingCompany: string) => {
  return request({
    url: `/orders/seller/${orderId}/ship`,
    method: 'post',
    params: { trackingNumber, shippingCompany }
  })
}

export const confirmReceipt = (orderId: number) => {
  return request({
    url: `/orders/customer/${orderId}/confirm`,
    method: 'post'
  })
}

export const cancelOrder = (orderId: number) => {
  return request({
    url: `/orders/customer/${orderId}/cancel`,
    method: 'post'
  })
} 