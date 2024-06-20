/*
 * @Author: your name
 * @Date: 2021-05-10 09:48:00
 * @LastEditTime: 2021-05-27 15:46:09
 * @LastEditors: your name
 * @Description: In User Settings Edit
 * @FilePath: \workspace-y9boot-v9.5.x-vue\y9vue-flowableUI\src\api\flowableUI\personTree.js
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
    url: "/vue/rolePerson/findAllPermUser",
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
    url: "/vue/rolePerson/findPermUserByName",
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
    url: "/vue/rolePerson/findCsUser",
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
    url: "/vue/rolePerson/findCsUserSearch",
    method: 'get',
    params: params
  });
}

