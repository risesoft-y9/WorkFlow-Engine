/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-05-27 16:33:53
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2021-08-03 11:06:46
 * @FilePath: \workspace-y9boot-9.5.x-vue\y9vue-flowableUI\src\api\flowableUI\userChoise.js
 */
import Request from '@/api/lib/request';

var flowableRequest = new Request();
//获取发送选人数据
export function getUserChoiseData(itemId,routeToTask,processDefinitionId,taskId,processInstanceId){
  const params = {
    itemId:itemId,
    routeToTask:routeToTask,
    processDefinitionId:processDefinitionId,
    taskId:taskId,
    processInstanceId:processInstanceId
  };
  return flowableRequest({
    url:'/vue/document/userChoiseData',
    method: 'get',
    params: params
  });
}

//获取发送选人人数
export function getUserCount(userChoice){
  const params = {
    userChoice:userChoice
  };
  return flowableRequest({
    url:'/vue/rolePerson/getUserCount',
    method: 'get',
    params: params
  });
}