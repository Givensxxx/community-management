import axios from "axios"
import { ElMessage } from "element-plus"

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "/api",
  timeout: 10000
})

request.interceptors.request.use(
  config => {
    const token = localStorage.getItem("token")
    if (token) {
      config.headers["Authorization"] = "Bearer " + token
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.msg || "璇锋眰閿欒")
      return Promise.reject(new Error(res.msg || "璇锋眰閿欒"))
    }
    return res
  },
  error => {
    const status = error.response?.status
    const msg = error.response?.data?.msg || error.message || "缃戠粶閿欒"
    if (status === 401) {
      ElMessage.error("鐧诲綍宸茶繃鏈燂紝璇烽噸鏂扮櫥褰?)
      localStorage.removeItem("token")
      localStorage.removeItem("userInfo")
      window.location.href = "/login"
    } else {
      ElMessage.error(msg)
    }
    return Promise.reject(error)
  }
)

export default request

