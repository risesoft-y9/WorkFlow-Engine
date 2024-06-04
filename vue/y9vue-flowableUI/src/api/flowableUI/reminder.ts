/*
 * @Author: your name
 * @Date: 2021-05-25 09:42:19
 * @LastEditTime: 2021-05-27 15:45:51
 * @LastEditors: your name
 * @Description: In User Settings Edit
 * @FilePath: \workspace-y9boot-v9.5.x-vue\y9vue-flowableUI\src\api\flowableUI\reminder.js
 */
import Request from '@/api/lib/request';

var flowableRequest = new Request();
//获取催办任务列表
export function taskList(processInstanceId,page,rows){
  const params = {
    processInstanceId:processInstanceId,
    page:page,
    rows:rows
  };
  return flowableRequest({
    url: "/vue/reminder/taskList",
    method: 'get',
    params: params
  });
}

//获取催办信息列表
export function reminderList(type,processInstanceId,page,rows){
  const params = {
    type:type,
    processInstanceId:processInstanceId,
    page:page,
    rows:rows
  };
  return flowableRequest({
    url: "/vue/reminder/reminderList",
    method: 'get',
    params: params
  });
}

//保存催办信息
export function saveReminder(processInstanceId,taskIds,msgContent){
  const params = {
    processInstanceId:processInstanceId,
    taskIds:taskIds,
    msgContent:msgContent
  };
  return flowableRequest({
    url: "/vue/reminder/saveReminder",
    method: 'POST',
    params: params
  });
}

//更新催办信息
export function updateReminder(id,msgContent){
  const params = {
    id:id,
    msgContent:msgContent
  };
  return flowableRequest({
    url: "/vue/reminder/updateReminder",
    method: 'POST',
    params: params
  });
}

//删除催办信息
export function deleteList(ids){
  const params = {
    ids:ids
  };
  return flowableRequest({
    url: "/vue/reminder/deleteList",
    method: 'post',
    params: params
  });
}

//我的任务催办
export function reminderMeList(taskId,page,rows){
  const params = {
    taskId:taskId,
    rows:rows,
    page:page
  };
  return flowableRequest({
    url: "/vue/reminder/reminderMeList",
    method: 'get',
    params: params
  });
}

//设置阅读时间
export function setReadTime(ids){
  const params = {
    ids:ids
  };
  return flowableRequest({
    url: "/vue/reminder/setReadTime",
    method: 'post',
    params: params
  });
}

//消息提醒：获取未办理的任务
export function remindTaskList(processInstanceId){
  const params = {
    processInstanceId
  };
  return flowableRequest({
    url: "/vue/remindInstance/taskList",
    method: 'get',
    params: params
  })
}

//获取任务节点信息和流程定义信息
export function getBpmList(processInstanceId){
  const params = {
    processInstanceId
  };
  return flowableRequest({
    url: "/vue/remindInstance/getBpmList",
    method: 'get',
    params: params
  })
}

export function saveRemindInstance(processInstanceId,taskIds,process,arriveTaskKey,completeTaskKey) {
  const params = {
    processInstanceId,
    taskIds,
    process,
    arriveTaskKey,
    completeTaskKey,
  };
  return flowableRequest({
    url: "/vue/remindInstance/saveRemindInstance",
    method: 'post',
    params: params
  })
}