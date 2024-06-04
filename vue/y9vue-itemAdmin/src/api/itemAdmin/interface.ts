/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2024-05-24 09:21:55
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6.4-xxx\y9-vue\y9vue-itemAdmin\src\api\itemAdmin\interface.ts
 */

import Request from "@/api/lib/request";
import qs from "qs";

var itemAdminRequest = new Request();

//获取接口列表
export function findInterfaceList(name, type, address){
  const params = {
    name,
    type,
    address
  };
  return itemAdminRequest({
    url: "/vue/interface/findInterfaceList",
    method: 'get',
    params: params
  });
}


//获取接口绑定事项列表
export function findByInterfaceId(id){
  const params = {
    id
  };
  return itemAdminRequest({
    url: "/vue/interface/findByInterfaceId",
    method: 'get',
    params: params
  });
}

//获取接口请求参数列表
export function findRequestParamsList(name, type, id){
  const params = {
    id,
    name,
    type
  };
  return itemAdminRequest({
    url: "/vue/interface/findRequestParamsList",
    method: 'get',
    params: params
  });
}

//获取接口响应参数列表
export function findResponseParamsList(name, id){
  const params = {
    id,
    name,
  };
  return itemAdminRequest({
    url: "/vue/interface/findResponseParamsList",
    method: 'get',
    params: params
  });
}

//保存接口
export function saveInterface(info){
  const data = qs.stringify(info);
  return itemAdminRequest({
    url: "/vue/interface/saveInterface",
    method: 'post',
    data: data
  });
}

//保存接口请求参数
export function saveRequestParams(info){
  const data = qs.stringify(info);
  return itemAdminRequest({
    url: "/vue/interface/saveRequestParams",
    method: 'post',
    data: data
  });
}

//保存接口响应参数
export function saveResponseParams(info){
  const data = qs.stringify(info);
  return itemAdminRequest({
    url: "/vue/interface/saveResponseParams",
    method: 'post',
    data: data
  });
}

//一键保存响应参数
export function saveAllResponseParams(interfaceId,jsonData){
  let formData = new FormData();
  formData.append("interfaceId", interfaceId);
  formData.append("jsonData", jsonData);
  return itemAdminRequest({
    url: "/vue/interface/saveAllResponseParams",
    method: 'post',
    data: formData
  });
}

//删除接口
export function removeInterface(id){
  const params = {
    id
  };
  return itemAdminRequest({
    url: "/vue/interface/removeInterface",
    method: 'post',
    params: params
  });
}

//删除接口请求参数
export function removeRequestParams(ids){
  const params = {
    ids
  };
  return itemAdminRequest({
    url: "/vue/interface/removeRequestParams",
    method: 'post',
    params: params
  });
}

//删除接口响应参数
export function removeResponseParams(ids){
  const params = {
    ids
  };
  return itemAdminRequest({
    url: "/vue/interface/removeResponseParams",
    method: 'post',
    params: params
  });
}