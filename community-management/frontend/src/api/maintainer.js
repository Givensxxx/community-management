import request from './request'

export const getPendingOrders = (params) => request.get('/order/pendingList', { params })
export const takeOrder = (orderId) => request.post(`/order/take/${orderId}`)
export const updateOrderStatus = (data) => request.post('/order/updateStatus', data)
export const getMaintainerOrders = (params) => request.get('/order/maintainerOrders', { params })
export const getOrderStatistics = () => request.get('/order/statistics')
export const getOrderDetail = (id) => request.get(`/order/detail/${id}`)
export const getOrderLogs = (orderId) => request.get(`/order/logs/${orderId}`)
