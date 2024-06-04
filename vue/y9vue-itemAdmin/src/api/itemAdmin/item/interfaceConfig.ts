/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2024-05-27 11:54:00
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6.4-xxx\y9-vue\y9vue-itemAdmin\src\api\itemAdmin\item\interfaceConfig.ts
 */

import Request from "@/api/lib/request";
import qs from "qs";

var itemAdminRequest = new Request();
//获取事项接口绑定列表
export function getBindList(itemId){
  const params = {
    itemId
  };
  return itemAdminRequest({
    url: "/vue/itemInterfaceBind/getBindList",
    method: 'get',
    params: params
  });
}

//删除接口绑定
export function removeBind(id){
  const params = {
    id
  };
  return itemAdminRequest({
    url: "/vue/itemInterfaceBind/removeBind",
    method: 'post',
    params: params
  });
}

//保存接口绑定
export function saveItemBind(itemId,interfaceIds){
  const params = {
    itemId,
    interfaceIds
  };
  const data = qs.stringify(params);
  return itemAdminRequest({
    url: "/vue/itemInterfaceBind/saveBind",
    method: 'post',
    data: data
  });
}


//获取任务节点信息和流程定义信息
export function getBpmList(itemId,interfaceId,processDefinitionId){
  const params = {
    itemId,
    interfaceId,
    processDefinitionId
  };
  return itemAdminRequest({
    url: "/vue/interfaceTaskBind/getBpmList",
    method: 'get',
    params: params
  });
}

//保存任务绑定
export function saveTaskBind(itemId,interfaceId,processDefinitionId,elementKey,condition){
  const params = {
    itemId,
    interfaceId,
    processDefinitionId,
    elementKey,
    condition
  };
  const data = qs.stringify(params);
  return itemAdminRequest({
    url: "/vue/interfaceTaskBind/saveBind",
    method: 'post',
    data: data
  });
}

//复制任务绑定
export function copyBind(itemId,interfaceId,processDefinitionId){
  const params = {
    itemId,
    interfaceId,
    processDefinitionId
  };
  const data = qs.stringify(params);
  return itemAdminRequest({
    url: "/vue/interfaceTaskBind/copyBind",
    method: 'post',
    data: data
  });
}

//获取参数绑定列表
export function getParamsBindList(itemId,interfaceId,type){
  const params = {
    itemId,
    interfaceId,
    type
  };
  return itemAdminRequest({
    url: "/vue/interfaceParamsBind/getBindList",
    method: 'get',
    params: params
  });
}

//获取参数绑定信息
export function getBindInfo(itemId,id){
  const params = {
    itemId,
    id
  };
  return itemAdminRequest({
    url: "/vue/interfaceParamsBind/getBindInfo",
    method: 'get',
    params: params
  });
}

//删除参数绑定
export function removeParamsBind(id){
  const params = {
    id
  };
  return itemAdminRequest({
    url: "/vue/interfaceParamsBind/removeBind",
    method: 'post',
    params: params
  });
}

//保存参数绑定
export function saveParamsBind(info){
  const data = qs.stringify(info);
  return itemAdminRequest({
    url: "/vue/interfaceParamsBind/saveBind",
    method: 'post',
    data: data
  });
}




