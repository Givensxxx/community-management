<template>
  <div>
    <h2>公告通知</h2>
    <el-timeline>
      <el-timeline-item v-for="item in noticeList" :key="item.id" :timestamp="formatDate(item.createTime)" placement="top">
        <el-card shadow="hover">
          <h4>{{ item.title }}</h4>
          <p class="notice-content">{{ item.content }}</p>
          <el-tag :type="item.type === 1 ? 'danger' : 'primary'" size="small">{{ item.type === 1 ? '重要' : '普通' }}</el-tag>
        </el-card>
      </el-timeline-item>
    </el-timeline>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getNoticeList } from '@/api/notice'

const noticeList = ref([])

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString()
}

onMounted(async () => {
  try {
    const res = await getNoticeList()
    noticeList.value = res.data || []
  } catch (e) {}
})
</script>

<style scoped>
h4 { margin: 0 0 8px 0; color: #303133; }
.notice-content { color: #606266; line-height: 1.6; margin: 8px 0; }
</style>