import request from '@/utils/request'

export function getTopNTopics(data) {
  return request({
    url: "/analysis/top",
    method: "get",
    params: data
  })
}

export function getRaceChartData(data) {
  return request({
    url: "/analysis/race",
    method: "get",
    params: data
  })
}