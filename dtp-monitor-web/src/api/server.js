import request from "@/utils/request";

export function getServerList(data) {
    return request({
        url: '/console/server/v1/getServerList',
        method: 'get',
        params: data
    })
}

export function insertServer(data) {
    return request({
        url: '/console/server/v1/insertServer',
        method: 'post',
        data: data
    })
}

export function updateServer(data) {
    return request({
        url: '/console/server/v1/updateServer',
        method: 'post',
        data: data
    })
}

export function deleteServer(data) {
    return request({
        url: '/console/server/v1/deleteServer',
        method: 'post',
        data: data
    })
}