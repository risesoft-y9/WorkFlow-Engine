/*
 * @Descripttion:
 * @version:
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2021-06-17 16:44:27
 * @FilePath: \workspace-y9boot-9.5.x-vue\y9vue-itemAdmin\src\api\itemAdmin\item\viewConfig.js
 */

import Request from '@/api/lib/request';
import qs from 'qs';

var itemAdminRequest = new Request();

//获取视图列表
export function getViewList(itemId, viewType) {
    const params = {
        viewType: viewType,
        itemId: itemId
    };
    return itemAdminRequest({
        url: '/vue/itemViewConf/findByItemId',
        method: 'get',
        params: params
    });
}

//获取视图类型列表
export function getViewTypeList() {
    const params = {};
    return itemAdminRequest({
        url: '/vue/viewType/listAll',
        method: 'get',
        params: params
    });
}

//获取视图信息
export function getViewInfo(id, itemId) {
    const params = {
        itemId: itemId,
        id: id
    };
    return itemAdminRequest({
        url: '/vue/itemViewConf/newOrModify',
        method: 'get',
        params: params
    });
}

//获取表列数据
export function getColumns(tableName, itemId) {
    const params = {
        tableName,
        itemId
    };
    return itemAdminRequest({
        url: '/vue/itemViewConf/getColumns',
        method: 'get',
        params: params
    });
}

//保存视图信息
export function saveView(itemViewConf) {
    const data = qs.stringify(itemViewConf);
    return itemAdminRequest({
        url: '/vue/itemViewConf/saveOrUpdate',
        method: 'post',
        data: data
    });
}

/**
 * 复制所选其他视图列至当前的视图列表下
 * @param ids
 * @param viewType
 * @returns
 */
export function copyView(ids, viewType) {
    const params = {
        ids: ids,
        viewType: viewType
    };
    return itemAdminRequest({
        url: '/vue/itemViewConf/copyView',
        method: 'post',
        params: params
    });
}

//删除视图
export function removeView(ids) {
    const params = {
        ids: ids
    };
    return itemAdminRequest({
        url: '/vue/itemViewConf/removeView',
        method: 'post',
        params: params
    });
}

//保存排序
export function saveOrder(idAndTabIndexs) {
    const params = {
        idAndTabIndexs: idAndTabIndexs
    };
    const data = qs.stringify(params);
    return itemAdminRequest({
        url: '/vue/itemViewConf/saveOrder',
        method: 'post',
        data: data
    });
}
