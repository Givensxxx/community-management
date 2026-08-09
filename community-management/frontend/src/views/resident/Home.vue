<template>
  <div class="home-page">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card class="quick-repair" shadow="hover" @click="goRepair">
          <div class="repair-icon">
            <el-icon :size="48" color="#409eff"><Tools /></el-icon>
          </div>
          <h3>快速报修</h3>
          <p>点击提交您的维修申请，我们将尽快处理</p>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card" shadow="hover">
          <h4>我的工单</h4>
          <div class="stat-number">{{ orderCount }}</div>
        </el-card>
      </el-col>
    </el-row>
    <el-card class="notice-section" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>最新公告</span>
          <el-button text @click="$router.push('/resident/notice')">查看更多</el-button>
        </div>
      </template>
      <el-timeline>
        <el-timeline-item v-for="item in noticeList" :key="item.id" :timestamp="formatDate(item.createTime)">
          {{ item.title }}
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue"
import { useRouter } from "vue-router"
import { Tools } from "@element-plus/icons-vue"
import { getNoticeList } from "@/api/notice"
import { getMyOrders } from "@/api/order"

const router = useRouter()
const orderCount = ref(0)
const noticeList = ref([])

const goRepair = () => router.push("/resident/repair")

const formatDate = (date) => {
  if (!date) return ""
  return new Date(date).toLocaleDateString()
}

onMounted(async () => {
  try {
    const noticeRes = await getNoticeList()
    noticeList.value = noticeRes.data || []
    const orderRes = await getMyOrders({ page: 1, size: 1 })
    orderCount.value = orderRes.data?.total || 0
  } catch (e) {}
})
</script>

<style scoped>
.home-page { padding: 0; }
.quick-repair { text-align: center; cursor: pointer; transition: all 0.3s; }
.quick-repair:hover { transform: translateY(-4px); box-shadow: 0 8px 16px rgba(0,0,0,0.1); }
.repair-icon { margin-bottom: 16px; }
.quick-repair h3 { margin: 8px 0; color: #303133; }
.quick-repair p { color: #909399; font-size: 14px; }
.stat-card { text-align: center; }
.stat-card h4 { color: #606266; margin-bottom: 12px; }
.stat-number { font-size: 36px; font-weight: bold; color: #409eff; }
.notice-section { margin-top: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
