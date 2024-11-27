/*
 * @Descripttion:
 * @version:
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2023-06-15 15:51:12
 * @FilePath: \workspace-y9boot-9.5.x-vue\y9vue-processAdmin\src\api\processAdmin\processDeploy.js
 */

import Request from '@/api/lib/request4Process';

var processAdminRequest = new Request();

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

//获取流程变量
export function getProcessXml(params) {
    return processAdminRequest({
        url: '/vue/repository/processInstanceXml',
        method: 'get',
        params: params
    });
}
