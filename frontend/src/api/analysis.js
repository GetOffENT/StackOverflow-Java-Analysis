import request from "@/utils/request";

export function getDateRange() {
  return request({
    url: "/analysis/date-range",
    method: "get",
  });
}

export function getTopNTopicsByEngagementOfUserWithHigherReputation(params) {
  return request({
    url: "/analysis/topic/engagement/top",
    method: "get",
    params,
  });
}

export function getTopNErrorsAndExceptions(params) {
  return request({
    url: "/analysis/error-and-exception/top",
    method: "get",
    params,
  })
}