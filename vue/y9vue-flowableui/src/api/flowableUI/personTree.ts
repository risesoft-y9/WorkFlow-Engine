/*
 * @Author: your name
 * @Date: 2021-05-10 09:48:00
 * @LastEditTime: 2026-06-29 14:47:44
 * @LastEditors: mengjuhua
 * @Description: 发送选人接口 
 * @FilePath: \y9-flowable\vue\y9vue-flowableUI\src\api\flowableUI\personTree.ts
 */
import Request from '@/api/lib/request';

var flowableRequest = new Request();

//获取发送选人数据
export function findAllPermUser(itemId, processDefinitionId, taskDefKey, principalType, processInstanceId, id) {
    const params = {
        itemId: itemId,
        processDefinitionId: processDefinitionId,
        taskDefKey: taskDefKey,
        principalType: principalType,
        processInstanceId: processInstanceId,
        id: id
    };
    return flowableRequest({
        url: '/vue/rolePerson/findAllPermUser',
        method: 'get',
        params: params
    });
}

//搜索发送选人数据
export function findPermUserByName(param) {
    const params = {
        itemId: param.itemId,
        processDefinitionId: param.processDefinitionId,
        taskDefKey: param.taskDefKey,
        principalType: param.principalType,
        processInstanceId: param.processInstanceId,
        name: param.name
    };
    return flowableRequest({
        url: '/vue/rolePerson/findPermUserByName',
        method: 'get',
        params: params
    });
}

//获取抄送选人数据
export function findCsUser(principalType, processInstanceId, id) {
    const params = {
        principalType: principalType,
        processInstanceId: processInstanceId,
        id: id
    };
    return flowableRequest({
        url: '/vue/rolePerson/findCsUser',
        method: 'get',
        params: params
    });
}

//搜索抄送选人数据
export function findCsUserSearch(param) {
    const params = {
        principalType: param.principalType,
        processInstanceId: param.processInstanceId,
        name: param.name
    };
    return flowableRequest({
        url: '/vue/rolePerson/findCsUserSearch',
        method: 'get',
        params: params
    });
}
