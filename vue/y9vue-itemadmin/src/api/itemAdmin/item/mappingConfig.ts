/*
 * @Descripttion:
 * @version:
 * @Author: zhangchongjie
 * @Date: 2022-07-13 10:09:45
 * @LastEditors: mengjuhua
 * @LastEditTime: 2026-01-08 14:06:06
 * @FilePath: \vue\y9vue-itemAdmin\src\api\itemAdmin\item\mappingConfig.ts
 */

import Request from '@/api/lib/request';
import qs from 'qs';

var itemAdminRequest = new Request();

//获取映射列表
export function getList(itemId, mappingId) {
    const params = {
        mappingId: mappingId,
        itemId: itemId
    };
    return itemAdminRequest({
        url: '/vue/itemMappingConf/getList',
        method: 'get',
        params: params
    });
}

//获取映射信息
export function getConfInfo(id, itemId, mappingItemId) {
    const params = {
        itemId: itemId,
        mappingItemId: mappingItemId,
        id: id
    };
    return itemAdminRequest({
        url: '/vue/itemMappingConf/getConfInfo',
        method: 'get',
        params: params
    });
}

//获取表列数据
export function getColumns(tableName) {
    const params = {
        tableName: tableName
    };
    return itemAdminRequest({
        url: '/vue/itemMappingConf/getColumns',
        method: 'get',
        params: params
    });
}

//保存映射信息
export function saveOrUpdate(itemMappingConf) {
    const data = qs.stringify(itemMappingConf);
    return itemAdminRequest({
        url: '/vue/itemMappingConf/saveOrUpdate',
        method: 'post',
        data: data
    });
}

//删除映射
export function remove(ids) {
    const params = {
        ids: ids
    };
    return itemAdminRequest({
        url: '/vue/itemMappingConf/remove',
        method: 'post',
        params: params
    });
}
