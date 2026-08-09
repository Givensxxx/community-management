import { defineStore } from "pinia"
import { ref, computed } from "vue"

export const useUserStore = defineStore("user", () => {
  // state
  const userInfo = ref({})
  const token = ref(localStorage.getItem("token") || "")
  const role = ref(Number(localStorage.getItem("role")) || 0)

  // getters
  const isLogin = computed(() => !!token.value)

  // actions
  const setUserInfo = (data) => {
    userInfo.value = data || {}
  }
  const setToken = (t) => {
    token.value = t
    localStorage.setItem("token", t)
  }
  const setRole = (r) => {
    role.value = r
    localStorage.setItem("role", r)
  }
  const clearUserInfo = () => {
    userInfo.value = {}
    token.value = ""
    role.value = 0
    localStorage.removeItem("token")
    localStorage.removeItem("role")
    localStorage.removeItem("userInfo")
  }
  const logout = () => {
    clearUserInfo()
  }

  return {
    userInfo,
    token,
    role,
    isLogin,
    setUserInfo,
    setToken,
    setRole,
    clearUserInfo,
    logout
  }
})
