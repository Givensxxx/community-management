<template>
  <div>
    <h2>分类管理</h2>
    <el-card shadow="never">
      <el-button type="success" @click="showAdd = true">新增分类</el-button>
      <el-table :data="tableData" stripe v-loading="loading" style="margin-top: 20px;">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="分类名称" />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="edit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="del(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="showAdd" title="新增/编辑分类" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" /></el-form-item>
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
import { getAdminCategoryList, saveCategory, deleteCategory } from '@/api/admin'

const tableData = ref([])
const loading = ref(false)
const showAdd = ref(false)
const form = ref({ id: null, name: '', sortOrder: 0 })

const loadData = async () => {
  loading.value = true
  try {
    const res = await getAdminCategoryList()
    tableData.value = res.data || []
  } catch (e) {}
  loading.value = false
}

const edit = (row) => {
  form.value = { ...row }
  showAdd.value = true
}

const submit = async () => {
  try {
    await saveCategory(form.value)
    ElMessage.success('保存成功')
    showAdd.value = false
    loadData()
  } catch (e) {}
}

const del = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该分类？', '提示', { type: 'warning' })
    await deleteCategory(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {}
}

onMounted(loadData)
</script>