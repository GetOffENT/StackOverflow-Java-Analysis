import request from '@/utils/request'

export function getTopNTopics(data) {
  return request({
    url: "/tag/top",
    method: "get",
    params: data
  })
}

export function getRaceChartData(data) {
  return request({
    url: "/tag/race",
    method: "get",
    params: data
  })
}