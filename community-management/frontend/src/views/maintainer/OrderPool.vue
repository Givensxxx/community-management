<template>
  <div>
    <h2>工单大厅</h2>
    <el-card shadow="never" class="table-card">
      <el-table :data="tableData" stripe v-loading="loading" @row-click="showDetail">
        <el-table-column prop="orderNo" label="工单号" width="150" />
        <el-table-column prop="address" label="地址" min-width="150" />
        <el-table-column prop="urgency" label="紧急度" width="90">
          <template #default="{ row }">
            <el-tag :type="urgencyType(row.urgency)">{{ row.urgency }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="180">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click.stop="handleTake(row)">接单</el-button>
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

    <!-- 详情抽屉 -->
    <el-drawer v-model="detailVisible" title="工单详情" size="500px">
      <el-descriptions :column="1" v-if="currentRow">
        <el-descriptions-item label="工单号">{{ currentRow.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="报修人">{{ currentRow.userName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentRow.userPhone }}</el-descriptions-item>
        <el-descriptions-item label="地址">{{ currentRow.address }}</el-descriptions-item>
        <el-descriptions-item label="紧急度">
          <el-tag :type="urgencyType(currentRow.urgency)">{{ currentRow.urgency }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="故障描述">{{ currentRow.description }}</el-descriptions-item>
        <el-descriptions-item label="故障图片" v-if="currentRow.images">
          <div class="image-list">
            <el-image
              v-for="(img, index) in currentRow.images.split(',')"
              :key="index"
              :src="img"
              :preview-src-list="currentRow.images.split(',')"
              fit="cover"
              class="preview-img"
            />
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ formatDate(currentRow.createTime) }}</el-descriptions-item>
      </el-descriptions>
      <div style="text-align: center; margin-top: 20px;" v-if="currentRow">
        <el-button type="primary" size="large" @click="handleTake(currentRow)">接单</el-button>
      </div>
    </el-drawer>
  </div>
</template>
<script setup>
import { ref, onMounted } from "vue"
import { ElMessage } from "element-plus"
import { getPendingOrders, takeOrder } from "@/api/maintainer"

const tableData = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const detailVisible = ref(false)
const currentRow = ref({})

const urgencyType = (u) => {
  const map = { "紧急": "danger", "一般": "warning", "低": "info" }
  return map[u] || "info"
}

const formatDate = (d) => d ? new Date(d).toLocaleString() : ""

const loadData = async () => {
  loading.value = true
  try {
    const res = await getPendingOrders({ page: page.value, size: size.value })
    const data = res.data || {}
    tableData.value = data.records || []
    total.value = data.total || 0
  } catch (e) {}
  loading.value = false
}

const handleTake = async (row) => {
  try {
    await takeOrder(row.id)
    ElMessage.success("接单成功")
    detailVisible.value = false
    loadData()
    window.dispatchEvent(new Event("refresh-stats"))
  } catch (e) {}
}

const showDetail = (row) => {
  currentRow.value = row
  detailVisible.value = true
}

onMounted(loadData)
</script>

<style scoped>
.table-card { margin-top: 20px; }
.pagination { margin-top: 20px; justify-content: flex-end; }
.preview-img { width: 80px; height: 80px; margin: 4px; border-radius: 4px; cursor: pointer; }
.image-list { display: flex; flex-wrap: wrap; }
</style>