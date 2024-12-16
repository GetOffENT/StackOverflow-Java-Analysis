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

export function getFirstAnswersWithCreateDate(params) {
  return request({
    url: "/analysis/answer-quality/create-date/first",
    method: "get",
    params,
  })
}

export function getAcceptedAnswersWithCreateDate(params) {
  return request({
    url: "/analysis/answer-quality/create-date/accepted",
    method: "get",
    params,
  })
}

export function getAnswersWithCreateDate(params) {
  return request({
    url: "/analysis/answer-quality/create-date/all",
    method: "get",
    params,
  })
}

export function getAnswersWithUserReputation(params) {
  return request({
    url: "/analysis/answer-quality/reputation",
    method: "get",
    params,
  })
}

export function getAnswersWithLength(params) {
  return request({
    url: "/analysis/answer-quality/length",
    method: "get",
    params,
  })
}