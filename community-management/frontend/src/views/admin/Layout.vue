<template>
  <el-container class="layout">
    <el-header style="display:flex;align-items:center;justify-content:space-between;background:#e6a23c;color:#fff;">
      <span>社区便民维护管理系统 - 管理后台</span>
      <el-button size="small" type="danger" @click="logout">退出</el-button>
    </el-header>
    <el-container>
      <el-aside width="200px">
        <el-menu router default-active="/admin/dashboard">
          <el-menu-item index="/admin/dashboard">首页</el-menu-item>
          <el-menu-item index="/admin/users">用户管理</el-menu-item>
          <el-menu-item index="/admin/orders">工单管理</el-menu-item>
          <el-menu-item index="/admin/categories">分类管理</el-menu-item>
          <el-menu-item index="/admin/notices">公告管理</el-menu-item>
          <el-menu-item index="/admin/statistics">数据统计</el-menu-item>
        </el-menu>
      </el-aside>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
const logout = () => {
  localStorage.clear()
  router.push('/login')
}
</script>

<style scoped>
.layout { height: 100vh; }
</style>
