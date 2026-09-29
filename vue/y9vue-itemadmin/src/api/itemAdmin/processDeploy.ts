/*
 * @Descripttion:
 * @version:
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: mengjuhua
 * @LastEditTime: 2026-01-08 14:09:09
 * @FilePath: \vue\y9vue-itemAdmin\src\api\itemAdmin\processDeploy.ts
 */

import processAdminRequest from '@/api/lib/request';

//获取部署列表
export function getDeployList() {
    const params = {
        resourceId: ''
    };
    return processAdminRequest({
        url: '/vue/repository/list',
        method: 'get',
        params: params
    });
}

//部署
export function deploy(file) {
    let formData = new FormData();
    formData.append('file', file);
    return processAdminRequest({
        url: '/vue/repository/deploy',
        method: 'post',
        data: formData
    });
}

//删除流程定义
export function deleteDeploy(deploymentId) {
    const params = {
        deploymentId: deploymentId
    };
    return processAdminRequest({
        url: '/vue/repository/delete',
        method: 'post',
        params: params
    });
}

//挂起、激活流程实例
export function switchSuspendOrActive(state, processDefinitionId) {
    const params = {
        state: state,
        processDefinitionId: processDefinitionId
    };
    return processAdminRequest({
        url: '/vue/repository/switchSuspendOrActive',
        method: 'post',
        params: params
    });
}

//流程图
export function graphTrace(resourceType, processInstanceId, processDefinitionId) {
    const params = {
        resourceType: resourceType,
        processInstanceId: processInstanceId,
        processDefinitionId: processDefinitionId
    };
    return processAdminRequest({
        url: '/vue/repository/process-instance',
        method: 'post',
        params: params
    });
}
