/*
 * @Author: your name
 * @Date: 2021-05-19 09:41:06
 * @LastEditTime: 2022-01-04 11:38:34
 * @LastEditors: zhangchongjie
 * @Description: In User Settings Edit
 * @FilePath: \workspace-y9boot-9.5-vue\y9vue-flowableUI\src\api\flowableUI\monitor.js
 */
import Request from '@/api/lib/request';

var flowableRequest = new Request();
//单位所有件
export function deptList(itemId,searchName,userName,state,year,page,rows) {
  const params = {
    itemId,
    searchName,
    userName,
    state,
    year,
    page,
    rows
  }
  return flowableRequest({
    url:"/vue/monitor/deptList",
    method: 'get',
    params: params
  });
}

//监控在办件
export function getmonitorDoingList(itemId,searchTerm,page,rows) {
  const params = {
    itemId,
    searchTerm,
    page,
    rows
  }
  return flowableRequest({
    url:"/vue/monitor/monitorDoingList",
    method: 'get',
    params: params
  });
}

//监控办结件
export function getmonitorDoneList(itemId,searchTerm,page,rows) {
  const params = {
    itemId,
    searchTerm,
    page,
    rows
  }
  return flowableRequest({
    url:"/vue/monitor/monitorDoneList",
    method: 'get',
    params: params
  });
}

//删除办件
export function removeProcess(processInstanceIds) {
  const params = {
    processInstanceIds
  };
  return flowableRequest({
    url: "/vue/monitor/removeProcess",
    method: 'post',
    params: params
  })
}

//监控办件
export function monitorBanjianList(searchName,itemId,userName,state,year,page,rows){
  const params = {
    searchName:searchName,
    itemId:itemId,
    userName:userName,
    state:state,
    year:year,
    page:page,
    rows:rows
  };
  return flowableRequest({
    url: "/vue/monitor/monitorBanjianList",
    method: 'get',
    params: params
  });
}

//监控阅件
export function monitorChaosongList(searchName,itemId,senderName,userName,state,year,page,rows){
  const params = {
    searchName:searchName,
    itemId:itemId,
    senderName:senderName,
    userName:userName,
    state:state,
    year:year,
    page:page,
    rows:rows
  };
  return flowableRequest({
    url: "/vue/monitor/monitorChaosongList",
    method: 'get',
    params: params
  });
}

//获取所有事项
export function getAllItemList(){
  const params = {};
  return flowableRequest({
    url: "/vue/monitor/getAllItemList",
    method: 'get',
    params: params
  });
}