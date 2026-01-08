/*
 * @Descripttion:
 * @version:
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: mengjuhua
 * @LastEditTime: 2026-01-08 14:09:25
 * @FilePath: \vue\y9vue-itemAdmin\src\api\itemAdmin\viewType.ts
 */

import Request from '@/api/lib/request';
import qs from 'qs';

var itemAdminRequest = new Request();

//获取意见框列表
export function viewTypeList(page, rows) {
    const params = {
        page: page,
        rows: rows
    };
    return itemAdminRequest({
        url: '/vue/viewType/list',
        method: 'get',
        params: params
    });
}

//获取意见框
export function getViewType(id) {
    const params = {
        id: id
    };
    return itemAdminRequest({
        url: '/vue/viewType/findById',
        method: 'get',
        params: params
    });
}

//保存意见框
export function saveOrUpdate(viewType) {
    const data = qs.stringify(viewType);
    return itemAdminRequest({
        url: '/vue/viewType/saveOrUpdate',
        method: 'post',
        data: data
    });
}

//删除意见框
export function removeViewType(ids) {
    const params = {
        ids: ids
    };
    return itemAdminRequest({
        url: '/vue/viewType/remove',
        method: 'post',
        params: params
    });
}
