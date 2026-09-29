/*
 * @Descripttion:
 * @version:
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: mengjuhua
 * @LastEditTime: 2026-01-08 14:04:37
 * @FilePath: \vue\y9vue-itemAdmin\src\api\itemAdmin\item\dataTransfer.ts
 */

import Request from '@/api/lib/request';

var itemAdminRequest = new Request();

//获取流程实例列表
export function getProcessInstanceList(processDefinitionId, itemId, page, rows) {
    const params = {
        processDefinitionId: processDefinitionId,
        itemId: itemId,
        page: page,
        rows: rows
    };
    return itemAdminRequest({
        url: '/vue/dataTransfer/getProcessInstanceList',
        method: 'get',
        params: params
    });
}

//数据迁移
export function dataTransfer(processDefinitionId, processInstanceId) {
    const params = {
        processDefinitionId: processDefinitionId,
        processInstanceId: processInstanceId
    };
    return itemAdminRequest({
        url: '/vue/dataTransfer/dataTransfer',
        method: 'post',
        params: params
    });
}
