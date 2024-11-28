/*
 * @Descripttion:
 * @version:
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2024-02-27 17:08:10
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-vue\y9vue-itemAdmin\src\api\itemAdmin\bpmnViewer.ts
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
