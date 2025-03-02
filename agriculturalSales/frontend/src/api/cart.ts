import request from '../utils/request'

export interface CartItem {
  "id": number,
  "productId": number,
  "productName": string,
  "productImage": null,
  "productDescription": string,
  "productPrice": number,
  "quantity": number,
  "totalPrice": number
}

export const getCartItems = () => {
  return request({
    url: '/cart',
    method: 'get'
  })
}

export const addToCart = (productId: number, quantity: number) => {
  return request({
    url: '/cart/add',
    method: 'post',
    data: { productId, quantity }
  })
}

export const updateCartItem = (id: number, productId: number, quantity: number) => {
  return request({
    url: `/cart/update/${id}`,
    method: 'put',
    data: { productId ,quantity }
  })
}

export const removeFromCart = (id: number) => {
  return request({
    url: `/cart/delete/${id}`,
    method: 'delete'
  })
} 

export const clearCart = () => {
  return request({
    url: '/cart/clear',
    method: 'delete'
  })
} 

export const checkoutItems = (itemIds: number[]) => {
  return request({
    url: '/cart/checkout',
    method: 'post',
    data: itemIds
  })
} 
