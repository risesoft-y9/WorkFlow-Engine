/*
 * @Descripttion:
 * @version:
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2021-06-18 09:19:07
 * @FilePath: \workspace-y9boot-9.5.x-vue\y9vue-itemAdmin\src\api\itemAdmin\item\buttonConfig.js
 */

import Request from '@/api/lib/request';
import qs from 'qs';

var itemAdminRequest = new Request();

//获取任务配置信息
export function getBpmList(processDefinitionId, itemId) {
    const params = {
        processDefinitionId: processDefinitionId,
        itemId: itemId
    };
    return itemAdminRequest({
        url: '/vue/itemStartNodeRole/getBpmList',
        method: 'get',
        params: params
    });
}

//复制上一版本配置
export function copyBind(itemId, processDefinitionId) {
    const params = {
        itemId: itemId,
        processDefinitionId: processDefinitionId
    };
    return itemAdminRequest({
        url: '/vue/itemStartNodeRole/copyBind',
        method: 'post',
        params: params
    });
}

//保存绑定角色
export function bindRole(itemId, processDefinitionId, taskDefKey, roleIds) {
    const params = {
        itemId: itemId,
        processDefinitionId: processDefinitionId,
        taskDefKey: taskDefKey,
        roleIds: roleIds
    };
    return itemAdminRequest({
        url: '/vue/itemStartNodeRole/saveRole',
        method: 'post',
        params: params
    });
}

//删除绑定角色
export function removeRole(itemId, processDefinitionId, taskDefKey, roleIds) {
    const params = {
        itemId: itemId,
        processDefinitionId: processDefinitionId,
        taskDefKey: taskDefKey,
        roleIds: roleIds
    };
    return itemAdminRequest({
        url: '/vue/itemStartNodeRole/remove',
        method: 'post',
        params: params
    });
}

//获取绑定角色列表
export function getRoleList(itemId, processDefinitionId, taskDefKey) {
    const params = {
        itemId: itemId,
        processDefinitionId: processDefinitionId,
        taskDefKey: taskDefKey
    };
    return itemAdminRequest({
        url: '/vue/itemStartNodeRole/list',
        method: 'get',
        params: params
    });
}

export function getNodeList(itemId, processDefinitionId) {
    const params = {
        itemId: itemId,
        processDefinitionId: processDefinitionId
    };
    return itemAdminRequest({
        url: '/vue/itemStartNodeRole/getNodeList',
        method: 'get',
        params: params
    });
}

export function saveOrder(idAndTabIndexs) {
    const params = {
        idAndTabIndexs: idAndTabIndexs
    };
    const data = qs.stringify(params);
    return itemAdminRequest({
        url: '/vue/itemStartNodeRole/saveOrder',
        method: 'post',
        data: data
    });
}
