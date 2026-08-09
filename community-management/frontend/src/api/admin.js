import request from './request'

export const getUserList = (params) => request.get('/admin/userList', { params })
export const updateUserStatus = (data) => request.post('/admin/userStatus', data)
export const deleteUser = (data) => request.post('/admin/userDelete', data)
export const addUser = (data) => request.post('/admin/userAdd', data)

export const getAdminOrderList = (params) => request.get('/admin/orderList', { params })
export const assignOrder = (data) => request.post('/admin/orderAssign', data)
export const closeOrder = (data) => request.post('/admin/orderClose', data)

export const getAdminCategoryList = () => request.get('/admin/categoryList')
export const saveCategory = (data) => request.post('/admin/categorySave', data)
export const deleteCategory = (id) => request.post(`/admin/categoryDelete/${id}`)

export const getAdminNoticeList = (params) => request.get('/admin/noticeList', { params })
export const saveNotice = (data) => request.post('/admin/noticeSave', data)
export const deleteNotice = (data) => request.post('/admin/noticeDelete', data)