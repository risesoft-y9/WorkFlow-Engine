/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2023-08-14 15:35:39
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-vue\y9vue-itemAdmin\src\api\processAdmin\processControl.ts
 */

import Request from "@/api/lib/request4Process";
import qs from "qs"; 

var processAdminRequest = new Request();  
//获取流程实例列表1
export function runningList(processInstanceId,page,rows){
  const params = {
    processInstanceId:processInstanceId,
    page:page,
    rows:rows 
  };
  return processAdminRequest({
    url: "/vue/processInstance/runningList",
    method: 'get',
    params: params
  });
}

//删除流程实例 
export function deleteProcessInstance(processInstanceId){
  const params = {
    processInstanceId:processInstanceId,
    type:"1",
    reason:""
  };
  return processAdminRequest({
    url: "/vue/processInstance/delete",
    method: 'post',
    params: params
  });
}

//挂起、激活流程实例
export function switchSuspendOrActive(state,processInstanceId){
  const params = {
    state:state,
    processInstanceId:processInstanceId
  };
  return processAdminRequest({
    url: "/vue/processInstance/switchSuspendOrActive",
    method: 'post',
    params: params
  });
}

//获取任务列表
export function getTaskList(processInstanceId){
  const params = {
    processInstanceId:processInstanceId
  };
  return processAdminRequest({
    url: "/vue/variable/getTaskList",
    method: 'get',
    params: params
  });
}

//获取任务变量列表
export function taskVarList(taskId){
  const params = {
    taskId:taskId
  };
  return processAdminRequest({
    url: "/vue/variable/taskVarList",
    method: 'get',
    params: params
  });
}

//删除任务变量
export function deleteTaskVar(taskId,key){
  const params = {
    taskId:taskId,
    key:key
  };
  return processAdminRequest({
    url: "/vue/variable/deleteTaskVar",
    method: 'post',
    params: params
  });
}

//获取任务变量
export function getTaskVariable(taskId,key){
  const params = {
    taskId:taskId,
    key:key
  };
  return processAdminRequest({
    url: "/vue/variable/getTaskVariable",
    method: 'get',
    params: params
  });
}

//保存任务变量
export function saveTaskVariable(type,taskId,key,value){
  const params = {
    type:type,
    taskId:taskId,
    key:key,
    value:value
  };
  return processAdminRequest({
    url: "/vue/variable/saveTaskVariable",
    method: 'post',
    params: params
  });
}

//获取流程变量列表
export function processVarList(processInstanceId){
  const params = {
    processInstanceId:processInstanceId
  };
  return processAdminRequest({
    url: "/vue/variable/processVarList",
    method: 'get',
    params: params
  });
}

//删除流程变量
export function deleteProcessVar(processInstanceId,key){
  const params = {
    processInstanceId:processInstanceId,
    key:key
  };
  return processAdminRequest({
    url: "/vue/variable/deleteProcessVar",
    method: 'post',
    params: params
  });
}

//获取流程变量
export function getProcessVariable(processInstanceId,key){
  const params = {
    processInstanceId:processInstanceId,
    key:key
  };
  return processAdminRequest({
    url: "/vue/variable/getProcessVariable",
    method: 'get',
    params: params
  });
}

//保存流程变量
export function saveProcessVariable(type,processInstanceId,key,value){
  const params = {
    type:type,
    processInstanceId:processInstanceId,
    key:key,
    value:value
  };
  return processAdminRequest({
    url: "/vue/variable/saveProcessVariable",
    method: 'post',
    params: params
  });
}

