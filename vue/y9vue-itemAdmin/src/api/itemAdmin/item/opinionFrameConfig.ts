/*
 * @Descripttion:
 * @version:
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2021-09-23 17:39:27
 * @FilePath: \workspace-y9boot-9.5.x-vue\y9vue-itemAdmin\src\api\itemAdmin\item\opinionFrameConfig.js
 */

import Request from '@/api/lib/request';

var itemAdminRequest = new Request();

//获取任务意见框权限信息
export function getBpmList(processDefinitionId, itemId) {
    const params = {
        processDefinitionId: processDefinitionId,
        itemId: itemId
    };
    return itemAdminRequest({
        url: '/vue/itemOpinionFrameBind/getBpmList',
        method: 'get',
        params: params
    });
}

//获取绑定的意见框列表
export function getBindList(itemId, processDefinitionId, taskDefKey) {
    const params = {
        itemId: itemId,
        processDefinitionId: processDefinitionId,
        taskDefKey: taskDefKey
    };
    return itemAdminRequest({
        url: '/vue/itemOpinionFrameBind/getBindList',
        method: 'get',
        params: params
    });
}

//删除意见框绑定
export function removeOpinionFrame(ids) {
    const params = {
        ids: ids
    };
    return itemAdminRequest({
        url: '/vue/itemOpinionFrameBind/remove',
        method: 'post',
        params: params
    });
}

//保存意见框绑定
export function bindOpinionFrame(itemId, processDefinitionId, taskDefKey, opinionFrameNameAndMarks) {
    const params = {
        itemId: itemId,
        processDefinitionId: processDefinitionId,
        taskDefKey: taskDefKey,
        opinionFrameNameAndMarks: opinionFrameNameAndMarks
    };
    return itemAdminRequest({
        url: '/vue/itemOpinionFrameBind/bindOpinionFrame',
        method: 'post',
        params: params
    });
}

//修改意见框绑定
export function saveModify(id, opinionFrameNameAndMarks) {
    const params = {
        id: id,
        opinionFrameNameAndMarks: opinionFrameNameAndMarks
    };
    return itemAdminRequest({
        url: '/vue/itemOpinionFrameBind/saveModify',
        method: 'post',
        params: params
    });
}

//复制上一版本意见框绑定
export function copyBind(itemId, processDefinitionId) {
    const params = {
        itemId: itemId,
        processDefinitionId: processDefinitionId
    };
    return itemAdminRequest({
        url: '/vue/itemOpinionFrameBind/copyBind',
        method: 'post',
        params: params
    });
}

//获取意见框列表
export function getOpinionFrameList(itemId, processDefinitionId, taskDefKey, page, rows) {
    const params = {
        itemId: itemId,
        processDefinitionId: processDefinitionId,
        taskDefKey: taskDefKey,
        page: page,
        rows: rows
    };
    return itemAdminRequest({
        url: '/vue/opinionFrame/list4NotUsed',
        method: 'get',
        params: params
    });
}

//搜索意见框
export function searchOpinionFrame(itemId, processDefinitionId, taskDefKey, keyword, page, rows) {
    const params = {
        itemId: itemId,
        processDefinitionId: processDefinitionId,
        taskDefKey: taskDefKey,
        keyword: keyword,
        page: page,
        rows: rows
    };
    return itemAdminRequest({
        url: '/vue/opinionFrame/search4NotUsed',
        method: 'get',
        params: params
    });
}

//保存意见框绑定角色
export function bindRole(roleIds, itemOpinionFrameId) {
    const params = {
        roleIds: roleIds,
        itemOpinionFrameId: itemOpinionFrameId
    };
    return itemAdminRequest({
        url: '/vue/itemOpinionFrameRole/bindRole',
        method: 'post',
        params: params
    });
}

//删除意见框绑定角色
export function removeRole(ids) {
    const params = {
        ids: ids
    };
    return itemAdminRequest({
        url: '/vue/itemOpinionFrameRole/remove',
        method: 'post',
        params: params
    });
}

//获取绑定角色列表
export function getRoleList(itemOpinionFrameId) {
    const params = {
        itemOpinionFrameId: itemOpinionFrameId
    };
    return itemAdminRequest({
        url: '/vue/itemOpinionFrameRole/list',
        method: 'get',
        params: params
    });
}

//改变意见框是否必签
export function changeSignOpinion(id, signOpinion) {
    const params = {
        id: id,
        signOpinion: signOpinion
    };
    return itemAdminRequest({
        url: '/vue/itemOpinionFrameBind/changeSignOpinion',
        method: 'post',
        params: params
    });
}
