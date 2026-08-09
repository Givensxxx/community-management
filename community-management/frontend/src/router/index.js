import { createRouter, createWebHistory } from "vue-router"

const routes = [
  { path: "/", redirect: "/login" },
  { path: "/login", component: () => import("@/views/login/index.vue") },
  {
    path: "/resident",
    component: () => import("@/views/resident/Layout.vue"),
    meta: { requiresRole: [1] },
    children: [
      { path: "home", component: () => import("@/views/resident/Home.vue") },
      { path: "repair", component: () => import("@/views/resident/RepairForm.vue") },
      { path: "orders", component: () => import("@/views/resident/MyOrders.vue") },
      { path: "notice", component: () => import("@/views/resident/Notice.vue") },
      { path: "profile", component: () => import("@/views/resident/Profile.vue") }
    ]
  },
  {
    path: "/maintainer",
    component: () => import("@/views/maintainer/Layout.vue"),
    meta: { requiresRole: [2] },
    children: [
      { path: "dashboard", component: () => import("@/views/maintainer/Dashboard.vue") },
      { path: "pool", component: () => import("@/views/maintainer/OrderPool.vue") },
      { path: "orders", component: () => import("@/views/maintainer/MyOrders.vue") }
    ]
  },
  {
    path: "/admin",
    component: () => import("@/views/admin/Layout.vue"),
    meta: { requiresRole: [3] },
    children: [
      { path: "dashboard", component: () => import("@/views/admin/Dashboard.vue") },
      { path: "users", component: () => import("@/views/admin/Users.vue") },
      { path: "orders", component: () => import("@/views/admin/Orders.vue") },
      { path: "categories", component: () => import("@/views/admin/Categories.vue") },
      { path: "notices", component: () => import("@/views/admin/Notices.vue") },
      { path: "statistics", component: () => import("@/views/admin/Statistics.vue") }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem("token")
  const role = parseInt(localStorage.getItem("role")) || 0
  if (to.path === "/login") {
    next()
    return
  }
  if (!token) {
    next("/login")
    return
  }
  const allowedRoles = to.meta?.requiresRole
  if (allowedRoles && !allowedRoles.includes(role)) {
    import('element-plus').then(({ ElMessage }) => { ElMessage.warning('您没有权限访问该页面') })
    next(role === 3 ? "/admin/dashboard" : role === 2 ? "/maintainer/dashboard" : "/resident/home")
    return
  }
  next()
})

export default router
