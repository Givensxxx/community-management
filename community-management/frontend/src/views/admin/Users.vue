<template>
  <div>
    <h2>用户管理</h2>
    <el-card shadow="never" class="filter-card">
      <el-form :inline="true">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="用户名" clearable />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="searchForm.role" placeholder="全部" clearable>
            <el-option label="居民" :value="1" />
            <el-option label="维修工" :value="2" />
            <el-option label="管理员" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">搜索</el-button>
          <el-button @click="searchForm = {}; loadData()">重置</el-button>
          <el-button type="success" @click="showAdd = true">新增用户</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="role" label="角色" width="90">
          <template #default="{ row }">
            <el-tag :type="roleType(row.role)">{{ roleText(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" :type="row.status === 1 ? 'danger' : 'success'" @click="toggleStatus(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button size="small" type="primary" @click="editUser(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="delUser(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="page" v-model:page-size="size" :total="total" :page-sizes="[10,20,50]"
        layout="total, sizes, prev, pager, next" @change="loadData" class="pagination" />
    </el-card>
    <el-dialog v-model="showAdd" title="新增/编辑用户" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名"><el-input v-model="form.username" /></el-form-item>
        <el-form-item label="密码"><el-input v-model="form.password" type="password" show-password /></el-form-item>
        <el-form-item label="昵称"><el-input v-model="form.nickname" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role">
            <el-option label="居民" :value="1" />
            <el-option label="维修工" :value="2" />
            <el-option label="管理员" :value="3" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAdd = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, updateUserStatus, deleteUser, addUser } from '@/api/admin'

const tableData = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const showAdd = ref(false)
const searchForm = ref({})
const form = reactive({ username: '', password: '', nickname: '', phone: '', role: 1 })

const roleType = (r) => r === 3 ? 'danger' : r === 2 ? 'warning' : 'info'
const roleText = (r) => r === 3 ? '管理员' : r === 2 ? '维修工' : '居民'

const loadData = async () => {
  loading.value = true
  try {
    const res = await getUserList({ page: page.value, size: size.value, ...searchForm.value })
    const data = res.data || {}
    tableData.value = data.records || []
    total.value = data.total || 0
  } catch (e) {}
  loading.value = false
}

const toggleStatus = async (row) => {
  try {
    await updateUserStatus({ id: row.id, status: row.status === 1 ? 0 : 1 })
    ElMessage.success('操作成功')
    loadData()
  } catch (e) {}
}

const delUser = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该用户？', '提示', { type: 'warning' })
    await deleteUser({ id: row.id })
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {}
}

const editUser = (row) => {
  Object.assign(form, { ...row, password: '' })
  showAdd.value = true
}

const submitForm = async () => {
  try {
    await addUser(form)
    ElMessage.success('添加成功')
    showAdd.value = false
    loadData()
  } catch (e) {}
}

onMounted(loadData)
</script>

<style scoped>
.filter-card { margin-bottom: 20px; }
.table-card { margin-top: 0; }
.pagination { margin-top: 20px; justify-content: flex-end; }
</style>