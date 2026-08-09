import request from './request'

export const createOrder = (data) => request.post('/order/create', data)
export const getMyOrders = (params) => request.get('/order/myList', { params })
export const getOrderDetail = (id) => request.get(`/order/detail/${id}`)
export const evaluateOrder = (data) => request.post('/order/evaluate', data)