/*
 * @Descripttion:
 * @version:
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: mengjuhua
 * @LastEditTime: 2026-01-08 14:07:52
 * @FilePath: \vue\y9vue-itemAdmin\src\api\itemAdmin\bpmnViewer.ts
 */

import Request from '@/api/lib/request';

var itemAdminRequest = new Request();

//获取任务列表
export function getTaskList(processInstanceId) {
    const params = {
        processInstanceId
    };
    return itemAdminRequest({
        url: '/vue/bpmnViewer/getTaskList',
        method: 'get',
        params: params
    });
}
