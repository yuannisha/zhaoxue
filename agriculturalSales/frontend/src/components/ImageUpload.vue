<template>
  <div class="image-upload">
    <el-upload
      accept=".jpg,.jpeg,.png"
      class="upload-container"
      action="http://localhost:8080/api/api/upload/image"
      :headers="uploadHeaders"
      :show-file-list="false"
      :on-success="handleSuccess"
      :on-error="handleError"
      :before-upload="beforeUpload"
    >
      <img 
        v-if="modelValue" 
        :src="getImageUrl(modelValue)"
        class="preview-image"
      >
      <el-icon v-else class="upload-icon"><Plus /></el-icon>
      <div class="upload-text">{{ modelValue ? '点击更换图片' : '点击上传图片' }}</div>
    </el-upload>
  </div>
</template>

<script setup lang="ts">
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { ref, defineProps, defineEmits } from 'vue'
import { getImageUrl } from '../utils/image'

// 声明 props 来接收父组件的 v-model
// @ts-ignore
const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  }
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
}>()

// 上传成功时更新父组件的 modelValue
const handleSuccess = (response: any) => {
  emit('update:modelValue', response.url)
}

const handleError = () => {
  ElMessage.error('上传失败')
}

const beforeUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 声明上传 headers
const uploadHeaders = ref({
  Authorization: `Bearer ${localStorage.getItem('token')}`
})
</script>

<style scoped>
.image-upload {
  width: 100%;
}

.upload-container {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 100%;
  height: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.upload-container:hover {
  border-color: #409EFF;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-icon {
  font-size: 28px;
  color: #8c939d;
}

.upload-text {
  color: #8c939d;
  margin-top: 8px;
  font-size: 14px;
}
</style>
