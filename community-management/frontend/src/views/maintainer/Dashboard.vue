<template>
  <div>
    <h2>首页统计</h2>
    <el-row :gutter="20" class="stat-row">
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card stat-pending">
          <div class="stat-icon"><el-icon :size="40"><Clock /></el-icon></div>
          <div class="stat-num">{{ stats.pendingCount }}</div>
          <div class="stat-label">待处理</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card stat-completed">
          <div class="stat-icon"><el-icon :size="40"><Check /></el-icon></div>
          <div class="stat-num">{{ stats.completedCount }}</div>
          <div class="stat-label">已完成</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card stat-rejected">
          <div class="stat-icon"><el-icon :size="40"><Close /></el-icon></div>
          <div class="stat-num">{{ stats.rejectedCount }}</div>
          <div class="stat-label">已驳回</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Clock, Check, Close } from '@element-plus/icons-vue'
import { getOrderStatistics } from '@/api/maintainer'

const stats = ref({ pendingCount: 0, completedCount: 0, rejectedCount: 0 })

onMounted(async () => {
  try {
    const res = await getOrderStatistics()
    stats.value = res.data || { pendingCount: 0, completedCount: 0, rejectedCount: 0 }
  } catch (e) {}
})
</script>

<style scoped>
.stat-row { margin-top: 20px; }
.stat-card { text-align: center; padding: 20px; color: #fff; }
.stat-pending { background: linear-gradient(135deg, #409eff, #79bbff); }
.stat-completed { background: linear-gradient(135deg, #67c23a, #95d475); }
.stat-rejected { background: linear-gradient(135deg, #f56c6c, #fab6b6); }
.stat-icon { margin-bottom: 12px; }
.stat-num { font-size: 36px; font-weight: bold; }
.stat-label { font-size: 14px; margin-top: 8px; }
</style>
