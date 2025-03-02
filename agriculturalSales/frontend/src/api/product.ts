import request from '../utils/request'

export enum ProductType {
  VEGETABLE = 'VEGETABLE',
  FRUIT = 'FRUIT',
  GRAIN = 'GRAIN',
  MEAT = 'MEAT',
  SEAFOOD = 'SEAFOOD',
  OTHER = 'OTHER'
}

export const ProductTypeLabels = {
  [ProductType.VEGETABLE]: '蔬菜',
  [ProductType.FRUIT]: '水果',
  [ProductType.GRAIN]: '粮食',
  [ProductType.MEAT]: '肉类',
  [ProductType.SEAFOOD]: '海鲜',
  [ProductType.OTHER]: '其他'
}

export interface Product {
  id: number
  name: string
  description: string
  price: number
  stock: number
  image: string
  type: ProductType
  sellerId: number
  createdAt: string
  updatedAt: string
}

export interface ProductSearchParams {
  keyword?: string
  minPrice?: number
  maxPrice?: number
  type?: ProductType
  page?: number
  size?: number
  sort?: string
}

export const searchProducts = (params?: ProductSearchParams) => {
  return request({
    url: '/products/get/all',
    method: 'get',
    params
  })
}

export const getProducts = (params?: any) => {
  return request({
    url: '/products/get/all',
    method: 'get',
    params
  })
}

export const getSellerProducts = (params?: any) => {
  return request({
    url: '/products/seller/get/all',
    method: 'get',
    params
  })
}

export const createProduct = (data: Partial<Product>) => {
  return request({
    url: '/products/create',
    method: 'post',
    data
  })
}

export const updateProduct = (id: number, data: Partial<Product>) => {
  return request({
    url: `/products/update/${id}`,
    method: 'put',
    data
  })
}

export const deleteProduct = (id: number) => {
  return request({
    url: `/products/delete/${id}`,
    method: 'delete'
  })
} 