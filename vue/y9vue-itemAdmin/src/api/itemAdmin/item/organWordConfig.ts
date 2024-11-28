/*
 * @Descripttion:
 * @version:
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2021-06-18 09:19:07
 * @FilePath: \workspace-y9boot-9.5.x-vue\y9vue-itemAdmin\src\api\itemAdmin\item\organWordConfig.js
 */

import Request from '@/api/lib/request';

var itemAdminRequest = new Request();

//获取任务编号配置信息
export function getBpmList(processDefinitionId, itemId) {
    const params = {
        processDefinitionId: processDefinitionId,
        itemId: itemId
    };
    return itemAdminRequest({
        url: '/vue/itemOrganWordBind/getBpmList',
        method: 'get',
        params: params
    });
}

//获取绑定的编号列表
export function getBindList(itemId, processDefinitionId, taskDefKey) {
    const params = {
        itemId: itemId,
        processDefinitionId: processDefinitionId,
        taskDefKey: taskDefKey
    };
    return itemAdminRequest({
        url: '/vue/itemOrganWordBind/getBindList',
        method: 'get',
        params: params
    });
}

//删除编号绑定
export function removeBind(id) {
    const params = {
        id: id
    };
    return itemAdminRequest({
        url: '/vue/itemOrganWordBind/removeBind',
        method: 'post',
        params: params
    });
}

//保存编号绑定
export function saveBind(itemId, processDefinitionId, taskDefKey, custom) {
    const params = {
        itemId: itemId,
        processDefinitionId: processDefinitionId,
        taskDefKey: taskDefKey,
        custom: custom
    };
    return itemAdminRequest({
        url: '/vue/itemOrganWordBind/saveBind',
        method: 'post',
        params: params
    });
}

//获取编号列表
export function getOrganWordList(itemId, processDefinitionId) {
    const params = {
        itemId: itemId,
        processDefinitionId: processDefinitionId
    };
    return itemAdminRequest({
        url: '/vue/itemOrganWordBind/getOrganWordList',
        method: 'get',
        params: params
    });
}

//复制上一版本编号配置
export function copyBind(itemId, processDefinitionId) {
    const params = {
        itemId: itemId,
        processDefinitionId: processDefinitionId
    };
    return itemAdminRequest({
        url: '/vue/itemOrganWordBind/copyBind',
        method: 'post',
        params: params
    });
}

//保存编号绑定角色
export function bindRole(roleIds, itemOrganWordBindId) {
    const params = {
        roleIds: roleIds,
        itemOrganWordBindId: itemOrganWordBindId
    };
    return itemAdminRequest({
        url: '/vue/itemOrganWordRole/bindRole',
        method: 'post',
        params: params
    });
}

//删除编号绑定的角色
export function removeRole(ids) {
    const params = {
        ids: ids
    };
    return itemAdminRequest({
        url: '/vue/itemOrganWordRole/remove',
        method: 'post',
        params: params
    });
}

//获取绑定角色列表
export function getRoleList(itemOrganWordBindId) {
    const params = {
        itemOrganWordBindId: itemOrganWordBindId
    };
    return itemAdminRequest({
        url: '/vue/itemOrganWordRole/list',
        method: 'get',
        params: params
    });
}
