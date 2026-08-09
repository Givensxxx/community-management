<template>
  <div>
    <h2>个人中心</h2>
    <el-card shadow="hover" class="profile-card">
      <el-form :model="form" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="楼栋">
          <el-input v-model="form.building" />
        </el-form-item>
        <el-form-item label="房号">
          <el-input v-model="form.roomNo" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveProfile">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserInfo, updateUser } from '@/api/user'

const form = ref({
  username: '',
  nickname: '',
  phone: '',
  building: '',
  roomNo: ''
})

const fetchUserInfo = async () => {
  try {
    const res = await getUserInfo()
    if (res.data) {
      form.value = { ...res.data }
    }
  } catch (e) {}
}

const saveProfile = async () => {
  try {
    await updateUser(form.value)
    ElMessage.success('保存成功')
    fetchUserInfo()
  } catch (e) {
    ElMessage.error(e.response?.data?.msg || '保存失败')
  }
}

onMounted(fetchUserInfo)
</script>

<style scoped>
.profile-card { max-width: 500px; margin-top: 20px; }
</style>