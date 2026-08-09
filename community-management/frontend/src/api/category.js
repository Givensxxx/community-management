import request from './request'

export const getCategoryList = () => request.get('/category/list')