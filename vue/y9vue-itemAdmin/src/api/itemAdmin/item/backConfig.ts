/*
 * @Descripttion:
 * @version:
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2026-06-01 11:41:05
 * @FilePath: \y9-vue\y9vue-itemAdmin\src\api\itemAdmin\item\buttonConfig.ts
 */

import Request from '@/api/lib/request';
import qs from 'qs';

var itemAdminRequest = new Request();
//获取任务退回配置信息
export function getBpmList(processDefinitionId, itemId) {
    const params = {
        processDefinitionId,
        itemId
    };
    return itemAdminRequest({
        url: '/vue/itemBackTaskBind/getBpmList',
        method: 'get',
        params: params
    });
}

//获取绑定的退回任务列表
export function getBindList(itemId, processDefinitionId, taskDefKey) {
    const params = {
        itemId,
        processDefinitionId,
        taskDefKey
    };
    return itemAdminRequest({
        url: '/vue/itemBackTaskBind/getBindList',
        method: 'get',
        params: params
    });
}

//删除绑定
export function removeBind(itemId, processDefinitionId, taskDefKey,removeTaskKey) {
    const params = {
        itemId,
        processDefinitionId,
        taskDefKey,
        removeTaskKey
    };
    return itemAdminRequest({
        url: '/vue/itemBackTaskBind/removeBind',
        method: 'post',
        params: params
    });
}

//保存退回任务绑定
export function saveBindTask(itemId, processDefinitionId, taskDefKey, bindTaskDefKey) {
    const params = {
        itemId,
        processDefinitionId,
        taskDefKey,
        bindTaskDefKey
    };
    return itemAdminRequest({
        url: '/vue/itemBackTaskBind/saveBindTask',
        method: 'post',
        params: params
    });
}

