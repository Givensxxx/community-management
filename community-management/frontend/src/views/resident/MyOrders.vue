<template>
  <div>
    <h2>我的工单</h2>
    <el-card class="filter-card" shadow="never">
      <el-form :inline="true">
        <el-form-item label="状态筛选">
          <el-select v-model="filterStatus" placeholder="全部" clearable @change="handleFilter">
            <el-option label="全部" value="" />
            <el-option label="待接单" value="0" />
            <el-option label="维修中" value="1" />
            <el-option label="已完成" value="4" />
            <el-option label="已驳回" value="5" />
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never">
      <el-table :data="orderList" stripe v-loading="loading">
        <el-table-column prop="orderNo" label="工单号" width="150" />
        <el-table-column prop="categoryName" label="分类" width="150" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="address" label="地址" min-width="150" />
        <el-table-column prop="createTime" label="提交时间" width="160">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="showDetail(row)">详情</el-button>
            <el-button v-if="row.status === 4" type="primary" size="small" @click="openEvaluate(row)">评价</el-button>
            <el-button v-if="row.status === 5" type="primary" size="small" @click="reSubmit(row)">重新提交</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @change="loadData"
        class="pagination"
      />
    </el-card>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyOrders } from '@/api/user'

const orderList = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const filterStatus = ref('')

const statusMap = {
  0: { text: '待接单', type: 'info' },
  1: { text: '维修中', type: 'warning' },
  2: { text: '维修中', type: 'warning' },
  3: { text: '维修中', type: 'warning' },
  4: { text: '已完成', type: 'success' },
  5: { text: '已驳回', type: 'danger' },
  6: { text: '已评价', type: 'success' }
}

const statusText = (s) => statusMap[s]?.text || '未知'
const statusType = (s) => statusMap[s]?.type || 'info'
const formatDate = (d) => d ? new Date(d).toLocaleString() : ''

const loadData = async () => {
  loading.value = true
  try {
    const params = { page: page.value, size: size.value }
    if (filterStatus.value !== '') { params.status = filterStatus.value }
    const res = await getMyOrders(params)
    const data = res.data || {}
    orderList.value = data.records || []
    total.value = data.total || 0
  } catch (e) {}
  loading.value = false
}

const handleFilter = () => {
  page.value = 1
  loadData()
}

const showDetail = (row) => {
  console.log('detail', row)
}

const openEvaluate = (row) => {
  console.log('evaluate', row)
}

const reSubmit = (row) => {
  console.log('resubmit', row)
}

onMounted(loadData)
</script>

<style scoped>
.filter-card { margin-bottom: 20px; }
.pagination { margin-top: 20px; justify-content: flex-end; }
</style>
