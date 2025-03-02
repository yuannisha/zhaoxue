import request from '../utils/request'

export interface LoginData {
  phone: string
  password: string
}

export interface RegisterData {
  username: string
  phone: string
  password: string
  role: string
}

export const login = (data: LoginData) => {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

export const register = (data: RegisterData) => {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
} 