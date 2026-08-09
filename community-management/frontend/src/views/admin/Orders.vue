<template>
  <div>
    <h2>工单管理</h2>
    <el-card shadow="never" class="filter-card">
      <el-form :inline="true">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="待接单" :value="0" />
            <el-option label="已接单" :value="1" />
            <el-option label="上门中" :value="2" />
            <el-option label="维修中" :value="3" />
            <el-option label="已完成" :value="4" />
            <el-option label="已驳回" :value="5" />
            <el-option label="已评价" :value="6" />
          </el-select>
        </el-form-item>
        <el-form-item label="楼栋">
          <el-input v-model="searchForm.building" placeholder="楼栋" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">搜索</el-button>
          <el-button @click="searchForm = {}; loadData()">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="orderNo" label="工单号" width="160" />
        <el-table-column prop="address" label="地址" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="180">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="showDetail(row)">详情</el-button>
            <el-button size="small" type="danger" @click="closeOrder(row)">关闭</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="page" v-model:page-size="size" :total="total" :page-sizes="[10,20,50]"
        layout="total, sizes, prev, pager, next" @change="loadData" class="pagination" />
    </el-card>
    <el-drawer v-model="drawerVisible" title="工单详情" size="500px">
      <div v-if="detail">
        <p><strong>工单号:</strong> {{ detail.orderNo }}</p>
        <p><strong>地址:</strong> {{ detail.address }}</p>
        <p><strong>描述:</strong> {{ detail.description }}</p>
        <p><strong>状态:</strong> {{ statusText(detail.status) }}</p>
        <p><strong>提交时间:</strong> {{ formatDate(detail.createTime) }}</p>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAdminOrderList, closeOrder } from '@/api/admin'

const tableData = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const searchForm = ref({})
const drawerVisible = ref(false)
const detail = ref(null)

const statusType = (s) => {
  const map = { 0: 'info', 1: 'warning', 2: 'warning', 3: '', 4: 'success', 5: 'danger', 6: 'success' }
  return map[s] || 'info'
}
const statusText = (s) => {
  const map = { 0: '待接单', 1: '已接单', 2: '上门中', 3: '维修中', 4: '已完成', 5: '已驳回', 6: '已评价' }
  return map[s] || '未知'
}
const formatDate = (d) => d ? new Date(d).toLocaleString() : ''

const loadData = async () => {
  loading.value = true
  try {
    const res = await getAdminOrderList({ page: page.value, size: size.value, ...searchForm.value })
    const data = res.data || {}
    tableData.value = data.records || []
    total.value = data.total || 0
  } catch (e) {}
  loading.value = false
}

const showDetail = (row) => {
  detail.value = row
  drawerVisible.value = true
}

const closeOrd = async (row) => {
  try {
    await closeOrder({ id: row.id })
    ElMessage.success('关闭成功')
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