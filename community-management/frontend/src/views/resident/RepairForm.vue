<template>
  <div>
    <h2>在线报修</h2>
    <el-card class="repair-form" shadow="never">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="报修分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择报修分类" style="width: 100%">
            <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="紧急程度" prop="urgency">
          <el-radio-group v-model="form.urgency">
            <el-radio label="1">紧急</el-radio>
            <el-radio label="2">一般</el-radio>
            <el-radio label="3">低</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="报修地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入详细地址（如：1栋101室）" />
        </el-form-item>
        <el-form-item label="问题描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请详细描述问题情况" />
        </el-form-item>
        <el-form-item label="图片上传">
          <el-upload
            action="#"
            list-type="picture-card"
            :auto-upload="false"
            :on-change="handleImageChange"
            :on-remove="handleImageRemove"
            :file-list="fileList"
            :limit="5"
            accept="image/*"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">最多上传5张图片</div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm">提交报修</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getCategoryList } from '@/api/category'
import { createOrder } from '@/api/order'

const formRef = ref()
const categories = ref([])
const fileList = ref([])

const form = ref({
  categoryId: null,
  description: '',
  address: '',
  urgency: '2',
  images: ''
})

const rules = {
  categoryId: [{ required: true, message: '请选择报修分类', trigger: 'change' }],
  address: [{ required: true, message: '请输入报修地址', trigger: 'blur' }],
  description: [{ required: true, message: '请输入问题描述', trigger: 'blur' }]
}

const handleImageChange = (file, list) => {
  fileList.value = list
}

const handleImageRemove = (file, list) => {
  fileList.value = list
}

const submitForm = async () => {
  await formRef.value.validate()
  try {
    const imageUrls = fileList.value.map(f => f.url || f.name).join(',')
    await createOrder({ ...form.value, images: imageUrls })
    ElMessage.success('报修提交成功')
    resetForm()
  } catch (e) {
    ElMessage.error(e.response?.data?.msg || '提交失败')
  }
}

const resetForm = () => {
  form.value = { categoryId: null, description: '', address: '', urgency: '2', images: '' }
  fileList.value = []
  formRef.value?.resetFields()
}

onMounted(async () => {
  try {
    const res = await getCategoryList()
    categories.value = res.data || []
  } catch (e) {}
})
</script>

<style scoped>
.repair-form { max-width: 700px; margin-top: 20px; }
.upload-tip { font-size: 12px; color: #909399; margin-top: 8px; }
</style>