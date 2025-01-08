import request from '@/utils/request'

// 查询频道列表
export function listChannels(query) {
  return request({
    url: '/cms/channels/list',
    method: 'get',
    params: query
  })
}

// 查询频道详细
export function getChannels(id) {
  return request({
    url: '/cms/channels/' + id,
    method: 'get'
  })
}

// 新增频道
export function addChannels(data) {
  return request({
    url: '/cms/channels',
    method: 'post',
    data: data
  })
}

// 修改频道
export function updateChannels(data) {
  return request({
    url: '/cms/channels',
    method: 'put',
    data: data
  })
}

// 删除频道
export function delChannels(id) {
  return request({
    url: '/cms/channels/' + id,
    method: 'delete'
  })
}
