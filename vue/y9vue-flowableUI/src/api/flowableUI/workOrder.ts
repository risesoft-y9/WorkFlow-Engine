/*
 * @Author: your name
 * @Date: 2021-05-14 15:35:06
 * @LastEditTime: 2021-06-10 17:04:35
 * @LastEditors: zhangchongjie
 * @Description: In User Settings Edit
 * @FilePath: \workspace-y9boot-9.5.x-vue\y9vue-flowableUI\src\api\flowableUI\workOrder.js
 */
import Request from '@/api/lib/request';

var flowableRequest = new Request();
//获取工单计数
export function getCount(){
  const params = {};
  return flowableRequest({
    url:'/vue/workOrder/getCount',
    method: 'get',
    params: params
  });
}

//获取草稿列表
export function draftList(searchTerm,page,rows){
  const params = {
    searchTerm:searchTerm,
    page:page,
    rows:rows
  };
  return flowableRequest({
    url:'/vue/workOrder/draftList',
    method: 'get',
    params: params
  });
}

//删除草稿
export function deleteDraft(processSerialNumber){
  const params = {
    processSerialNumber:processSerialNumber
  };
  return flowableRequest({
    url:'/vue/workOrder/deleteDraft',
    method: 'post',
    params: params
  });
}

//获取未处理列表
export function todoList(listType,searchTerm,page,rows){
  const params = {
    listType:listType,
    searchTerm:searchTerm,
    page:page,
    rows:rows
  };
  return flowableRequest({
    url:'/vue/workOrder/todoList',
    method: 'get',
    params: params
  });
}

//获取处理中列表
export function doingList(listType,searchTerm,page,rows){
  const params = {
    listType:listType,
    searchTerm:searchTerm,
    page:page,
    rows:rows
  };
  return flowableRequest({
    url:'/vue/workOrder/doingList',
    method: 'get',
    params: params
  });
}

//获取已处理列表
export function doneList(listType,searchTerm,page,rows){
  const params = {
    listType:listType,
    searchTerm:searchTerm,
    page:page,
    rows:rows
  };
  return flowableRequest({
    url:'/vue/workOrder/doneList',
    method: 'get',
    params: params
  });
}


//获取附件列表
export function getAttachmentList(processSerialNumber,page,rows){
  const params = {
    processSerialNumber:processSerialNumber,
    page:page,
    rows:rows
  };
  return flowableRequest({
    url:'/vue/workOrder/getAttachmentList',
    method: 'get',
    params: params
  });
}

//上传附件
export function uploadFile(file,processSerialNumber){
  let formData = new FormData();
  formData.append("file", file);
  formData.append("processSerialNumber", processSerialNumber);
  return flowableRequest({
    url:'/vue/workOrder/uploadFile',
    method: 'post',
    data: formData
  });
}

//删除附件
export function delFile(ids){
  const params = {
    ids:ids
  };
  return flowableRequest({
    url:'/vue/workOrder/delFile',
    method: 'get',
    params: params
  });
}

//获取管理员打开未处理数据
export function openData(processSerialNumber,itembox,itemId){
  const params = {
    processSerialNumber:processSerialNumber,
    itembox:itembox,
    itemId:itemId
  };
  return flowableRequest({
    url:'/vue/workOrder/open',
    method: 'GET',
    params: params
  });
}

//获取打开未处理数据
export function openDetail(processSerialNumber){
  const params = {
    processSerialNumber:processSerialNumber
  };
  return flowableRequest({
    url:'/vue/workOrder/openDetail',
    method: 'GET',
    params: params
  });
}

//提交工单数据
export function workOrderSubmit(type,formdata){
  const params = {
    type:type,
    formdata:formdata
  };
  return flowableRequest({
    url:'/vue/workOrder/workOrderSubmit',
    method: 'post',
    params: params
  });
}

//发送
export function forwarding(itemId,processInstanceId,processDefinitionKey,processSerialNumber,userChoice,sponsorGuid,
                        routeToTaskId,level,number,documentTitle,isSendSms,isShuMing,smsContent){
  const params = {
    processSerialNumber:processSerialNumber,
    itemId:itemId,
    processInstanceId:processInstanceId,
    processDefinitionKey:processDefinitionKey,
    userChoice:userChoice,
    sponsorGuid:sponsorGuid,
    routeToTaskId:routeToTaskId,
    level:level,
    number:number,
    documentTitle:documentTitle,
    isSendSms:isSendSms,
    isShuMing:isShuMing,
    smsContent:smsContent
  };
  return flowableRequest({
    url:'/vue/workOrderAdmin/forwarding',
    method: 'post',
    params: params
  });
}

//办结工单
export function workOrderFinish(processSerialNumber,resultFeedback){
  const params = {
    processSerialNumber:processSerialNumber,
    resultFeedback:resultFeedback
  };
  return flowableRequest({
    url:'/vue/workOrderAdmin/workOrderFinish',
    method: 'post',
    params: params
  });
}