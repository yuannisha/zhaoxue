import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const username = ref(localStorage.getItem('username') || '')
  const role = ref(localStorage.getItem('role') || '')

  function setToken(newToken: string) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  function setUsername(newUsername: string) {
    username.value = newUsername
    localStorage.setItem('username', newUsername)
  }

  function setRole(newRole: string) {
    role.value = newRole
    localStorage.setItem('role', newRole)
  }

  function clearUserInfo() {
    token.value = ''
    username.value = ''
    role.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('role')
  }

  return {
    token,
    username,
    role,
    setToken,
    setUsername,
    setRole,
    clearUserInfo
  }
}) 