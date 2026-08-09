<template>
  <div>
    <h2>我的工单</h2>
    <el-card shadow="never" class="table-card">
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="orderNo" label="工单号" width="150" />
        <el-table-column prop="categoryName" label="分类" width="150" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :color="statusColor(row.status)" :style="{ color: '#fff' }">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="address" label="地址" min-width="150" />
        <el-table-column prop="createTime" label="提交时间" width="160">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="160">
          <template #default="{ row }">{{ formatDate(row.updateTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 1">
              <el-button type="primary" size="small" @click="updateStatus(row, 2)">上门中</el-button>
            </template>
            <template v-if="row.status === 2">
              <el-button type="primary" size="small" @click="updateStatus(row, 3)">维修中</el-button>
            </template>
            <template v-if="row.status === 3">
              <el-button type="success" size="small" @click="showCompleteDialog(row)">完成维修</el-button>
              <el-button type="danger" size="small" @click="showRejectDialog(row)">无法维修</el-button>
            </template>
            <template v-if="row.status >= 4">
              <el-button size="small" @click="showDetail(row)">查看详情</el-button>
            </template>
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

    <!-- 完成维修弹窗 -->
    <el-dialog v-model="completeVisible" title="完成维修" width="600px">
      <el-form :model="completeForm" label-width="100px">
        <el-form-item label="维修结果" required>
          <el-input v-model="completeForm.result" type="textarea" :rows="3" placeholder="请填写维修处理过程和结果" />
        </el-form-item>
        <el-form-item label="维修耗材">
          <el-input v-model="completeForm.materials" placeholder="如：灯泡x2、电线5米" />
        </el-form-item>
        <el-form-item label="补充说明">
          <el-input v-model="completeForm.remark" type="textarea" :rows="2" placeholder="补充说明，选填" />
        </el-form-item>
        <el-form-item label="完成图片">
          <el-upload
            v-model:file-list="completeForm.images"
            action="/api/upload"
            :headers="uploadHeaders"
            list-type="picture-card"
            :limit="5"
            :on-success="handleUploadSuccess"
            :on-preview="handlePicturePreview"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="completeVisible = false">取消</el-button>
        <el-button type="primary" @click="submitComplete" :loading="submitting">确认</el-button>
      </template>
    </el-dialog>

    <!-- 无法维修弹窗 -->
    <el-dialog v-model="rejectVisible" title="无法维修" width="500px">
      <el-form :model="rejectForm" label-width="100px">
        <el-form-item label="驳回原因" required>
          <el-input v-model="rejectForm.reason" type="textarea" :rows="4" placeholder="请说明无法维修的原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="danger" @click="submitReject" :loading="submitting">确认驳回</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="工单详情" width="600px">
      <el-descriptions :column="1" border v-if="currentRow">
        <el-descriptions-item label="工单号">{{ currentRow.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ statusText(currentRow.status) }}</el-descriptions-item>
        <el-descriptions-item label="地址">{{ currentRow.address }}</el-descriptions-item>
        <el-descriptions-item label="故障描述">{{ currentRow.description }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ formatDate(currentRow.createTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, onMounted, computed } from "vue"
import { ElMessage } from "element-plus"
import { Plus } from "@element-plus/icons-vue"
import { getMaintainerOrders, updateOrderStatus } from "@/api/maintainer"

const tableData = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)

// Dialog visibility
const completeVisible = ref(false)
const rejectVisible = ref(false)
const detailVisible = ref(false)
const currentRow = ref({})
const submitting = ref(false)

// Forms
const completeForm = ref({
  result: "",
  materials: "",
  remark: "",
  images: []
})

const rejectForm = ref({
  reason: ""
})

const uploadHeaders = computed(() => ({
  Authorization: "Bearer " + localStorage.getItem("token")
}))

const statusText = (s) => {
  const map = { 0: "待接单", 1: "已接单", 2: "上门中", 3: "维修中", 4: "已完成", 5: "已驳回", 6: "已评价" }
  return map[s] || "未知"
}

const statusColor = (s) => {
  const map = { 0: "#909399", 1: "#409EFF", 2: "#E6A23C", 3: "#F2C037", 4: "#67C23A", 5: "#F56C6C", 6: "#67C23A" }
  return map[s] || "#909399"
}

const formatDate = (d) => d ? new Date(d).toLocaleString() : ""

const loadData = async () => {
  loading.value = true
  try {
    const res = await getMaintainerOrders({ page: page.value, size: size.value })
    const data = res.data || {}
    tableData.value = data.records || []
    total.value = data.total || 0
  } catch (e) {}
  loading.value = false
}

const updateStatus = async (row, newStatus) => {
  try {
    await updateOrderStatus({ id: row.id, status: newStatus })
    ElMessage.success("状态更新成功")
    loadData()
  } catch (e) {}
}

const showCompleteDialog = (row) => {
  currentRow.value = row
  completeForm.value = { result: "", materials: "", remark: "", images: [] }
  completeVisible.value = true
}

const showRejectDialog = (row) => {
  currentRow.value = row
  rejectForm.value = { reason: "" }
  rejectVisible.value = true
}

const showDetail = (row) => {
  currentRow.value = row
  detailVisible.value = true
}

const handleUploadSuccess = (response, file) => {
  if (response.code === 200) {
    file.url = response.data.url
  }
}

const handlePicturePreview = (file) => {
  window.open(file.url || file.response?.data?.url, "_blank")
}

const submitComplete = async () => {
  if (!completeForm.value.result) {
    ElMessage.warning("请填写维修结果")
    return
  }
  submitting.value = true
  try {
    await updateOrderStatus({
      id: currentRow.value.id,
      status: 4,
      result: completeForm.value.result,
      materials: completeForm.value.materials,
      remark: completeForm.value.remark,
      resultImages: completeForm.value.images.map(f => f.url).join(",")
    })
    ElMessage.success("维修完成")
    completeVisible.value = false
    loadData()
  } catch (e) {}
  submitting.value = false
}

const submitReject = async () => {
  if (!rejectForm.value.reason) {
    ElMessage.warning("请填写驳回原因")
    return
  }
  submitting.value = true
  try {
    await updateOrderStatus({
      id: currentRow.value.id,
      status: 5,
      rejectReason: rejectForm.value.reason
    })
    ElMessage.success("已驳回")
    rejectVisible.value = false
    loadData()
  } catch (e) {}
  submitting.value = false
}

onMounted(loadData)
</script>

<style scoped>
.table-card { margin-top: 20px; }
.pagination { margin-top: 20px; justify-content: flex-end; }
</style>