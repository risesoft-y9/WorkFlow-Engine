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

//获取任务按钮配置信息
export function getBpmList(processDefinitionId, itemId) {
    const params = {
        processDefinitionId: processDefinitionId,
        itemId: itemId
    };
    return itemAdminRequest({
        url: '/vue/itemButtonBind/getBpmList',
        method: 'get',
        params: params
    });
}

//获取绑定的按钮列表
export function getBindList(itemId, processDefinitionId, taskDefKey, buttonType) {
    const params = {
        itemId: itemId,
        processDefinitionId: processDefinitionId,
        taskDefKey: taskDefKey,
        buttonType: buttonType
    };
    return itemAdminRequest({
        url: '/vue/itemButtonBind/getBindList',
        method: 'get',
        params: params
    });
}

//删除按钮绑定
export function removeButton(ids) {
    const params = {
        ids: ids
    };
    return itemAdminRequest({
        url: '/vue/itemButtonBind/removeBind',
        method: 'post',
        params: params
    });
}

//保存按钮绑定
export function saveBindButton(itemId, processDefinitionId, taskDefKey, buttonId, buttonType) {
    const params = {
        itemId: itemId,
        processDefinitionId: processDefinitionId,
        taskDefKey: taskDefKey,
        buttonId: buttonId,
        buttonType: buttonType
    };
    return itemAdminRequest({
        url: '/vue/itemButtonBind/saveBindButton',
        method: 'post',
        params: params
    });
}

//获取按钮列表
export function getButtonList(itemId, processDefinitionId, taskDefKey, buttonType) {
    const params = {
        itemId: itemId,
        processDefinitionId: processDefinitionId,
        taskDefKey: taskDefKey,
        buttonType: buttonType
    };
    return itemAdminRequest({
        url: '/vue/itemButtonBind/getButtonList',
        method: 'get',
        params: params
    });
}

//复制上一版本按钮配置
export function copyBind(itemId, processDefinitionId) {
    const params = {
        itemId: itemId,
        processDefinitionId: processDefinitionId
    };
    return itemAdminRequest({
        url: '/vue/itemButtonBind/copyBind',
        method: 'post',
        params: params
    });
}

//获取按钮排序列表
export function getButtonOrderList(itemId, processDefinitionId, taskDefKey, buttonType) {
    const params = {
        itemId: itemId,
        processDefinitionId: processDefinitionId,
        taskDefKey: taskDefKey,
        buttonType: buttonType
    };
    return itemAdminRequest({
        url: '/vue/itemButtonBind/getButtonOrderList',
        method: 'get',
        params: params
    });
}

//保存按钮排序
export function saveOrder(idAndTabIndexs) {
    const params = {
        idAndTabIndexs: idAndTabIndexs
    };
    const data = qs.stringify(params);
    return itemAdminRequest({
        url: '/vue/itemButtonBind/saveOrder',
        method: 'post',
        data: data
    });
}

//保存按钮绑定角色
export function bindRole(roleIds, itemButtonId) {
    const params = {
        roleIds: roleIds,
        itemButtonId: itemButtonId
    };
    return itemAdminRequest({
        url: '/vue/itemButtonRole/saveRole',
        method: 'post',
        params: params
    });
}

//删除按钮绑定角色
export function removeRole(ids) {
    const params = {
        ids: ids
    };
    return itemAdminRequest({
        url: '/vue/itemButtonRole/remove',
        method: 'post',
        params: params
    });
}

//获取绑定角色列表
export function getRoleList(itemButtonId) {
    const params = {
        itemButtonId: itemButtonId
    };
    return itemAdminRequest({
        url: '/vue/itemButtonRole/list',
        method: 'get',
        params: params
    });
}
