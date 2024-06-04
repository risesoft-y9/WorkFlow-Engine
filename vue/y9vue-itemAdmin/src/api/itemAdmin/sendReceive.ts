/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2021-07-02 10:23:27
 * @FilePath: \workspace-y9boot-9.5.x-vue\y9vue-itemAdmin\src\api\itemAdmin\sendReceive.js
 */

import Request from "@/api/lib/request";
import qs from "qs";

var itemAdminRequest = new Request();
//获取组织机构
export function getOrg(){
  const params = {};
  return itemAdminRequest({
    url: "/vue/sendReceive/getOrg",
    method: 'get',
    params: params
  });
}

export function getOrgChildTree(param){
  const params = {
    id:param.parentId,
    treeType:param.treeType
  };
  return itemAdminRequest({
    url: "/vue/sendReceive/getOrgChildTree",
    method: 'get',
    params: params
  });
}

//搜索组织架构
export function getOrgTree(param){
  const params = {
    id:param.parentId,
    treeType:param.treeType
  };
  return itemAdminRequest({
    url: "/vue/sendReceive/getOrgTree",
    method: 'get',
    params: params
  });
}

//搜索组织架构
export function orgTreeSearch(param){
  const params = {
    name:param.key,
    treeType:param.treeType
  };
  return itemAdminRequest({
    url: "/vue/sendReceive/searchOrgTree",
    method: 'get',
    params: params
  });
}

//设置取消收发部门
export function saveOrCancelDept(id,type){
  const params = {
    id:id,
    type:type
  };
  return itemAdminRequest({
    url: "/vue/sendReceive/saveOrCancelDept",
    method: 'post',
    params: params
  });
}

//获取收发员列表
export function personList(deptId){
  const params = {
    deptId:deptId
  };
  return itemAdminRequest({
    url: "/vue/sendReceive/personList",
    method: 'get',
    params: params
  });
}

//获取部门树
export function getDeptTree(param){
  const params = {
    id:param.parentId,
    deptId:param.deptId
  };
  return itemAdminRequest({
    url: "/vue/sendReceive/getDeptTree",
    method: 'get',
    params: params
  });
}

//部门树搜索
export function deptTreeSearch(name,deptId){
  const params = {
    name:name,
    deptId:deptId
  };
  return itemAdminRequest({
    url: "/vue/sendReceive/deptTreeSearch",
    method: 'get',
    params: params
  });
}

//设置收发员
export function savePerson(ids,deptId){
  const params = {
    ids:ids,
    deptId:deptId
  };
  return itemAdminRequest({
    url: "/vue/sendReceive/savePerson",
    method: 'post',
    params: params
  });
}

//删除收发员
export function delPerson(id){
  const params = {
    id:id
  };
  return itemAdminRequest({
    url: "/vue/sendReceive/delPerson",
    method: 'post',
    params: params
  });
}

//判断是否收发文单位
export function checkReceiveSend(deptId){
  const params = {
    deptId:deptId
  };
  return itemAdminRequest({
    url: "/vue/sendReceive/checkReceiveSend",
    method: 'post',
    params: params
  });
}

//设置、取消发文权限
export function setSend(send,ids){
  const params = {
    ids:ids,
    send:send
  };
  return itemAdminRequest({
    url: "/vue/sendReceive/setSend",
    method: 'post',
    params: params
  });
}

//设置、取消收文权限
export function setReceive(receive,ids){
  const params = {
    ids:ids,
    receive:receive
  };
  return itemAdminRequest({
    url: "/vue/sendReceive/setReceive",
    method: 'post',
    params: params
  });
}
