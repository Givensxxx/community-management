<template>
  <el-container class="maintainer-layout">
    <el-aside width="200px">
      <div class="logo">维修端</div>
      <el-menu router :default-active="$route.path" class="el-menu-vertical">
        <el-menu-item index="/maintainer/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>首页统计</span>
        </el-menu-item>
        <el-menu-item index="/maintainer/pool">
          <el-icon><List /></el-icon>
          <span>工单大厅</span>
        </el-menu-item>
        <el-menu-item index="/maintainer/orders">
          <el-icon><Document /></el-icon>
          <span>我的工单</span>
        </el-menu-item>
        <el-menu-item index="logout" @click="logout">
          <el-icon><SwitchButton /></el-icon>
          <span>退出登录</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header height="60px">
        <div class="header">
          <span>物业维修管理系统</span>
          <div class="user-info">
            <el-icon><User /></el-icon>
            <span class="username">{{ userStore.nickname || userStore.username }}</span>
          </div>
        </div>
      </el-header>
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user.js'
import { User, Odometer, List, Document, SwitchButton } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.maintainer-layout { height: 100vh; }
.logo {
  height: 60px; line-height: 60px; text-align: center;
  font-size: 18px; font-weight: bold; color: #fff;
  background: #67c23a;
}
.el-menu-vertical { border-right: none; height: calc(100% - 60px); }
.header {
  display: flex; align-items: center; justify-content: space-between;
  background: #fff; box-shadow: 0 1px 4px rgba(0,0,0,0.08);
  padding: 0 20px; height: 100%;
}
.user-info { display: flex; align-items: center; gap: 8px; }
.username { font-size: 14px; color: #606266; }
.main-content { background: #f5f7fa; padding: 20px; }
</style>
