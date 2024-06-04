/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2023-06-15 15:51:25
 * @FilePath: \workspace-y9boot-9.5.x-vue\y9vue-processAdmin\src\api\processAdmin\processModel.js
 */

import Request from "@/api/lib/request4Process";
import qs from "qs";

var processAdminRequest = new Request();
//获取部署列表
export function getModelList(){ 
  const params = {
    resourceId:""
  };
  return processAdminRequest({
    url: "/vue/processModel/getModelList",
    method: 'get',
    params: params
  });
}

/**
 * 获取流程设计XML
 */
export function getModelXml(modelId){ 
  const params = {
    modelId
  };
  return processAdminRequest({
    url: "/vue/processModel/getModelXml",
    method: 'get',
    params: params
  });
}

//删除流程定义
export function deleteModel(modelId){
  const params = {
    modelId:modelId
  };
  return processAdminRequest({
    url: "/vue/processModel/deleteModel",
    method: 'post',
    params: params
  });
}

//创建流程设计
export function createModel(name,key,description){
  const params = {
    name:name,
    key:key,
    description:description
  };
  return processAdminRequest({
    url: "/vue/processModel/create",
    method: 'post',
    params: params
  });
}

//部署流程设计
export function deployModel(modelId){
  const params = {
    modelId:modelId
  };
  return processAdminRequest({
    url: "/vue/processModel/deployModel",
    method: 'post',
    params: params
  });
}