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
export function getItemList(){
  const params = {};
  return flowableRequest({
    url: "/vue/document/getItemList",
    method: 'get',
    params: params
  });
}

//获取事项信息
export function getItem(itemId){
  const params = {
    itemId:itemId
  };
  return flowableRequest({
    url: "/vue/mian/getItem",
    method: 'get',
    params: params
  });
}

//获取角色权限
export function getRole(){
  const params = {};
  return flowableRequest({
    url: "/vue/mian/getRole",
    method: 'get',
    params: params
  });
}

//获取岗位列表
export function getPositionList(count,itemId){
  const params = {count:count,itemId:itemId};
  return flowableRequest({
    url: "/vue/mian/getPositionList",
    method: 'get',
    params: params
  });
}

//获取流程任务信息
export function getTaskOrProcessInfo(taskId,processInstanceId,type){
  const params = {
    taskId:taskId,
    processInstanceId:processInstanceId,
    type:type
  };
  return flowableRequest({
    url: "/vue/mian/getTaskOrProcessInfo",
    method: 'get',
    params: params
  });
}


//获取新建数据
export function addData(itemId){
  const params = {
    itemId:itemId
  };
  return flowableRequest({
    url: "/vue/document/add",
    method: 'get',
    params: params
  });
}

//获取表单初始化数据
export function getFormInitData(url,processSerialNumber){
  const params = {
    processSerialNumber:processSerialNumber
  };
  if(url == ""){
    url = "/vue/y9form/getInitData";
  }
  return flowableRequest({
    url: url,
    method: 'get',
    params: params
  });
}

//获取打开办件数据
export function getTodoData(processInstanceId,taskId,itemId,itembox){
  const params = {
    processInstanceId:processInstanceId,
    itemId:itemId,
    taskId:taskId,
    itembox:itembox,
  };
  return flowableRequest({
    url:'/vue/document/edit',
    method: 'get',
    params: params
  });
}

//获取阅件左侧计数
export function getReadCount(){
  const params = {};
  return flowableRequest({
    url:'/vue/mian/getReadCount',
    method: 'get',
    params: params
  });
}

