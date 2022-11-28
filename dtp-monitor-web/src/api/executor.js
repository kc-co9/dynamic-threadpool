import request from "@/utils/request";

export function getExecutorList(data) {
    return request({
        url: '/console/executor/v1/getExecutorList',
        method: 'get',
        params: data
    })
}

export function getExecutorDetail(data) {
    return request({
        url: '/console/executor/v1/getExecutorDetail',
        method: 'get',
        params: data
    })
}

export function configureExecutor(data) {
    return request({
        url: '/console/executor/v1/configureExecutor',
        method: 'post',
        data: data
    })
}