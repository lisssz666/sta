import request from '@/utils/request'

// 查询站点列表
export function listSites(query) {
  return request({
    url: '/cms/sites/list',
    method: 'get',
    params: query
  })
}

// 查询站点详细
export function getSites(id) {
  return request({
    url: '/cms/sites/' + id,
    method: 'get'
  })
}

// 新增站点
export function addSites(data) {
  return request({
    url: '/cms/sites',
    method: 'post',
    data: data
  })
}

// 修改站点
export function updateSites(data) {
  return request({
    url: '/cms/sites',
    method: 'put',
    data: data
  })
}

// 删除站点
export function delSites(id) {
  return request({
    url: '/cms/sites/' + id,
    method: 'delete'
  })
}
