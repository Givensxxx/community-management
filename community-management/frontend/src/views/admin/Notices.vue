<template>
  <div>
    <h2>公告管理</h2>
    <el-card shadow="never">
      <el-button type="success" @click="showAdd = true">新增公告</el-button>
      <el-table :data="tableData" stripe v-loading="loading" style="margin-top: 20px;">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="type" label="类型" width="90">
          <template #default="{ row }">
            <el-tag :type="row.type === 1 ? 'success' : 'info'">{{ row.type === 1 ? '重要' : '普通' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="edit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="del(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="showAdd" title="新增/编辑公告" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="4" /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.type">
            <el-option label="普通" :value="0" />
            <el-option label="重要" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option label="禁用" :value="0" />
            <el-option label="启用" :value="1" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAdd = false">取消</el-button>
        <el-button type="primary" @click="submit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminNoticeList, saveNotice, deleteNotice } from '@/api/admin'

const tableData = ref([])
const loading = ref(false)
const showAdd = ref(false)
const form = ref({ id: null, title: '', content: '', type: 0, status: 1 })

const loadData = async () => {
  loading.value = true
  try {
    const res = await getAdminNoticeList({ page: 1, size: 100 })
    const data = res.data || {}
    tableData.value = data.records || data || []
  } catch (e) {}
  loading.value = false
}

const edit = (row) => {
  form.value = { ...row }
  showAdd.value = true
}

const submit = async () => {
  try {
    await saveNotice(form.value)
    ElMessage.success('保存成功')
    showAdd.value = false
    loadData()
  } catch (e) {}
}

const del = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该公告？', '提示', { type: 'warning' })
    await deleteNotice({ id: row.id })
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {}
}

onMounted(loadData)
</script>