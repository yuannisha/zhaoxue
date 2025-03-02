const API_BASE_URL = 'http://localhost:8080/api'

export const getImageUrl = (path: string) => {
  if (!path) return ''
  return `${API_BASE_URL}${path}`
} 