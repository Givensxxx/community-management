
<template>
  <div class="login-container">
    <el-card class="login-card" shadow="always">
      <template #header>
        <h2 class="login-title">社区便民维护管理系统</h2>
      </template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="0">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" :prefix-icon="Lock" size="large" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" class="login-btn" @click="handleLogin" :loading="loading">登录</el-button>
        </el-form-item>
        <div class="register-link">
          <el-button type="default" @click="showRegister = true">还没有账号？立即注册</el-button>
        </div>
      </el-form>
    </el-card>

    <el-dialog v-model="showRegister" title="居民注册" width="500px" destroy-on-close>
      <el-form ref="regRef" :model="regForm" :rules="regRules" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="regForm.username" placeholder="用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="regForm.password" type="password" placeholder="密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="regForm.confirmPassword" type="password" placeholder="确认密码" show-password />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="regForm.nickname" placeholder="昵称" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="regForm.phone" placeholder="手机号" />
        </el-form-item>
        <el-form-item label="楼栋号" prop="building">
          <el-input v-model="regForm.building" placeholder="如：1栋" />
        </el-form-item>
        <el-form-item label="房间号" prop="roomNo">
          <el-input v-model="regForm.roomNo" placeholder="如：101" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRegister = false">取消</el-button>
        <el-button type="primary" @click="handleRegister" :loading="regLoading">注册</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue"
import { useRouter } from "vue-router"
import { useUserStore } from "@/store/user.js"
import { login, register } from "@/api/auth.js"
import { User, Lock } from "@element-plus/icons-vue"
import { ElMessage } from "element-plus"

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const regRef = ref()
const loading = ref(false)
const regLoading = ref(false)
const showRegister = ref(false)

const form = reactive({ username: "", password: "" })
const regForm = reactive({
  username: "", password: "", confirmPassword: "",
  nickname: "", phone: "", building: "", roomNo: ""
})

const rules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }]
}

const regRules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
  confirmPassword: [{ required: true, message: "请确认密码", trigger: "blur" }, {
    validator: (rule, value, callback) => {
      if (value !== regForm.password) callback(new Error("两次密码不一致"))
      else callback()
    }, trigger: "blur"
  }],
  nickname: [{ required: true, message: "请输入昵称", trigger: "blur" }],
  phone: [{ required: true, message: "请输入手机号", trigger: "blur" }],
  building: [{ required: true, message: "请输入楼栋号", trigger: "blur" }],
  roomNo: [{ required: true, message: "请输入房间号", trigger: "blur" }]
}

const handleLogin = async () => {
  try {
    await formRef.value.validate()
    loading.value = true
    const res = await login(form)
    const { token: t, role, username, nickname } = res.data
    userStore.setToken(t)
    userStore.setRole(role)
    userStore.setUserInfo({ username, nickname, role })
    ElMessage.success("登录成功")
    if (role === 1) router.push("/resident/home")
    else if (role === 2) router.push("/maintainer/dashboard")
    else if (role === 3) router.push("/admin/dashboard")
    else router.push("/")
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handleRegister = async () => {
  try {
    await regRef.value.validate()
    if (regForm.password !== regForm.confirmPassword) {
      ElMessage.error("两次密码不一致")
      return
    }
    regLoading.value = true
    await register({
      username: regForm.username,
      password: regForm.password,
      nickname: regForm.nickname,
      phone: regForm.phone,
      building: regForm.building,
      roomNo: regForm.roomNo
    })
    ElMessage.success("注册成功，请登录")
    showRegister.value = false
    form.username = regForm.username
  } catch (e) {
    console.error(e)
  } finally {
    regLoading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
  width: 420px;
  border-radius: 12px;
}
.login-title {
  text-align: center;
  margin: 0;
  font-size: 22px;
  color: #333;
}
.login-btn {
  width: 100%;
}
.register-link {
  text-align: center;
  margin-top: 12px;
}
</style>
