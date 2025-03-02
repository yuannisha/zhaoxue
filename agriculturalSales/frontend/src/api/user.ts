import request from '../utils/request'

export interface UserProfile {
  username: string
  phone: string
  realName: string
  email: string
  address: string
}

export const getUserProfile = () => {
  return request({
    url: '/profile/getProfile',
    method: 'get'
  })
}

export const updateUserProfile = (data: UserProfile) => {
  return request({
    url: '/profile/updateProfile',
    method: 'put',
    data
  })
} 