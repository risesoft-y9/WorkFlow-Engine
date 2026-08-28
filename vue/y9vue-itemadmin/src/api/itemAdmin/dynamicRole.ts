/*
 * @Descripttion:
 * @version:
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: mengjuhua
 * @LastEditTime: 2026-01-08 14:08:25
 * @FilePath: \vue\y9vue-itemAdmin\src\api\itemAdmin\dynamicRole.ts
 */

import Request from '@/api/lib/request';
import qs from 'qs';

var itemAdminRequest = new Request();

//获取动态角色列表
export function dynamicRoleList() {
    const params = {};
    return itemAdminRequest({
        url: '/vue/dynamicRole/dynamicRoleList',
        method: 'get',
        params: params
    });
}

//获取动态角色
export function getDynamicRole(id) {
    const params = {
        id: id
    };
    return itemAdminRequest({
        url: '/vue/dynamicRole/getDynamicRole',
        method: 'get',
        params: params
    });
}

//保存动态角色
export function saveOrUpdate(dynamicRole) {
    const data = qs.stringify(dynamicRole);
    return itemAdminRequest({
        url: '/vue/dynamicRole/saveOrUpdate',
        method: 'post',
        data: data
    });
}

//删除动态角色
export function removeDynamicRole(dynamicRoleIds) {
    const params = {
        dynamicRoleIds: dynamicRoleIds
    };
    return itemAdminRequest({
        url: '/vue/dynamicRole/remove',
        method: 'post',
        params: params
    });
}

/**
 *获取数字底座部门属性分类
 */
export function deptPropCategory() {
    const params = {};
    return itemAdminRequest({
        url: '/vue/dynamicRole/deptPropCategory',
        method: 'get',
        params: params
    });
}

/**
 * 获取数字底座公共角色
 */
export function publicRole() {
    const params = {};
    return itemAdminRequest({
        url: '/vue/dynamicRole/publicRole',
        method: 'get',
        params: params
    });
}

/**
 * 获取所有动态角色类路径
 */
export function getClasses() {
    const params = {
        packageName: 'net.risesoft.service.dynamicrole.impl'
    };
    return itemAdminRequest({
        url: '/vue/dynamicRole/getClasses',
        method: 'get',
        params: params
    });
}
