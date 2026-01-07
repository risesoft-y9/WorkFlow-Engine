/*
 * @Author: your name
 * @Date: 2021-05-19 09:41:06
 * @LastEditTime: 2021-07-02 16:00:47
 * @LastEditors: zhangchongjie
 * @Description: In User Settings Edit
 * @FilePath: \workspace-y9boot-9.5.x-vue\y9vue-flowableUI\src\api\flowableUI\index.js
 */
import Request from '@/api/lib/request';

var flowableRequest = new Request();
//获取事项列表
export function getItemList() {
    const params = {};
    return flowableRequest({
        url: "/vue/document/getItemList",
        method: 'get',
        params: params
    });
}

//获取事项信息
export function getItem(itemId) {
    const params = {
        itemId: itemId
    };
    return flowableRequest({
        url: "/vue/main/getItem",
        method: 'get',
        params: params
    });
}

//获取角色权限
export function getRole() {
    const params = {};
    return flowableRequest({
        url: "/vue/main/getRole",
        method: 'get',
        params: params
    });
}

//获取岗位列表
export function getPositionList(count, itemId) {
    const params = { count: count, itemId: itemId };
    return flowableRequest({
        url: "/vue/main/getPositionList",
        method: 'get',
        params: params
    });
}

//获取流程任务信息
export function getTaskOrProcessInfo(taskId, processInstanceId, type) {
    const params = {
        taskId: taskId,
        processInstanceId: processInstanceId,
        type: type
    };
    return flowableRequest({
        url: "/vue/main/getTaskOrProcessInfo",
        method: 'get',
        params: params
    });
}


//获取新建数据
export function addData(itemId) {
    const params = {
        itemId: itemId
    };
    return flowableRequest({
        url: "/vue/document/add",
        method: 'get',
        params: params
    });
}

//获取表单初始化数据
export function getFormInitData(url, processSerialNumber) {
    const params = {
        processSerialNumber: processSerialNumber
    };
    if (url == "") {
        url = "/vue/y9form/getInitData";
    }
    return flowableRequest({
        url: url,
        method: 'get',
        params: params
    });
}

//获取待办件数据
export function getTodoData(taskId) {
    const params = {
        taskId: taskId
    };
    return flowableRequest({
        url: '/vue/document/edit/todo',
        method: 'get',
        params: params
    });
}

export function getDoingData(documentId, processInstanceId) {
    const params = {
        documentId: documentId,
        processInstanceId: processInstanceId
    };
    return flowableRequest({
        url: '/vue/document/edit/doing',
        method: 'get',
        params: params
    });
}

export function getDoneData(documentId, processInstanceId) {
    const params = {
        documentId: documentId,
        processInstanceId: processInstanceId
    };
    return flowableRequest({
        url: '/vue/document/edit/done',
        method: 'get',
        params: params
    });
}

export function getMonitorDoingData(documentId, processInstanceId) {
    const params = {
        documentId: documentId,
        processInstanceId: processInstanceId
    };
    return flowableRequest({
        url: '/vue/document/edit/doingAdmin',
        method: 'get',
        params: params
    });
}

export function getMonitorDoneData(documentId, processInstanceId) {
    const params = {
        documentId: documentId,
        processInstanceId: processInstanceId
    };
    return flowableRequest({
        url: '/vue/document/edit/doneAdmin',
        method: 'get',
        params: params
    });
}

export function getMonitorChaoSongData(documentId, processInstanceId) {
    const params = {
        documentId: documentId,
        processInstanceId: processInstanceId
    };
    return flowableRequest({
        url: '/vue/document/edit/doneAdmin',
        method: 'get',
        params: params
    });
}

//获取阅件左侧计数
export function getReadCount() {
    const params = {};
    return flowableRequest({
        url: '/vue/main/getReadCount',
        method: 'get',
        params: params
    });
}

