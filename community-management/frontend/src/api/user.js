import request from './request'

export const getUserInfo = () => request.get('/user/info')
export const updateUser = (data) => request.post('/user/update', data)