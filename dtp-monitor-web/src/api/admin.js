import request from "@/utils/request";

export function signIn(data) {
    return request({
        url: '/console/administrator/v1/signIn',
        method: 'post',
        data: data
    })
}

export function signOut(data) {
    return request({
        url: '/console/administrator/v1/signOut',
        method: 'post',
    })
}

export function getAdministratorInfo(data) {
    return request({
        url: '/console/administrator/v1/getAdministratorInfo',
        method: 'get',
        params: data
    })
}

export function getAdministrators(data) {
    return request({
        url: '/console/administrator/v1/getAdministrators',
        method: 'get',
        params: data
    })
}

export function insertAdministrator(data) {
    return request({
        url: '/console/administrator/v1/insertAdministrator',
        method: 'post',
        data: data
    })
}

export function updateAdministrator(data) {
    return request({
        url: '/console/administrator/v1/updateAdministrator',
        method: 'post',
        data: data
    })
}

export function deleteAdministrator(data) {
    return request({
        url: '/console/administrator/v1/deleteAdministrator',
        method: 'post',
        data: data
    })
}