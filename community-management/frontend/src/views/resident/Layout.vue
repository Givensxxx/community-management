<template>
  <el-container class="resident-layout">
    <el-aside width="200px">
      <div class="logo">居民端</div>
      <el-menu router :default-active="$route.path" class="el-menu-vertical">
        <el-menu-item index="/resident/home">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/resident/repair">
          <el-icon><Tools /></el-icon>
          <span>在线报修</span>
        </el-menu-item>
        <el-menu-item index="/resident/orders">
          <el-icon><Document /></el-icon>
          <span>我的工单</span>
        </el-menu-item>
        <el-menu-item index="/resident/notice">
          <el-icon><Bell /></el-icon>
          <span>公告通知</span>
        </el-menu-item>
        <el-menu-item index="/resident/profile">
          <el-icon><User /></el-icon>
          <span>个人中心</span>
        </el-menu-item>
        <el-menu-item index="logout" @click="logout">
          <el-icon><SwitchButton /></el-icon>
          <span>退出登录</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header height="50px" class="header">
        <span>{{ pageTitle }}</span>
        <div class="user-info">
          <el-avatar :size="28" :icon="UserFilled" />
          <span class="username">{{ userInfo.nickname || userInfo.username }}</span>
        </div>
      </el-header>
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { HomeFilled, Tools, Document, Bell, User, SwitchButton, UserFilled } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

const pageTitle = computed(() => {
  const titles = {
    '/resident/home': '首页',
    '/resident/repair': '在线报修',
    '/resident/orders': '我的工单',
    '/resident/notice': '公告通知',
    '/resident/profile': '个人中心'
  }
  return titles[route.path] || '居民端'
})

const logout = () => {
  localStorage.clear()
  router.push('/login')
}
</script>

<style scoped>
.resident-layout { height: 100vh; }
.logo {
  height: 60px; line-height: 60px; text-align: center;
  font-size: 18px; font-weight: bold; color: #fff;
  background: #409eff;
}
.el-menu-vertical { border-right: none; height: calc(100% - 60px); }
.header {
  display: flex; align-items: center; justify-content: space-between;
  background: #fff; box-shadow: 0 1px 4px rgba(0,0,0,0.08);
}
.user-info { display: flex; align-items: center; gap: 8px; }
.username { font-size: 14px; color: #606266; }
.main-content { background: #f5f7fa; padding: 20px; }
</style>