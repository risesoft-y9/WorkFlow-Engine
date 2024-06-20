/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2022-07-12 11:46:00
 * @FilePath: \workspace-y9boot-9.5-vuee:\workspace-y9boot-9.6-vue\y9vue-itemAdmin\src\api\itemAdmin\item\formConfig.ts
 */

import Request from "@/api/lib/request";
import qs from "qs";
var itemAdminRequest = new Request();
//获取任务配置信息
export function getBpmList(processDefinitionId,itemId){
  const params = {
    processDefinitionId:processDefinitionId,
    itemId:itemId
  };
  return itemAdminRequest({
    url: "/vue/y9form/item/getBpmList",
    method: 'get',
    params: params
  });
}

//获取绑定的表单列表
export function getBindList(itemId,procDefId,taskDefKey){
  const params = {
    itemId:itemId,
    procDefId:procDefId,
    taskDefKey:taskDefKey
  };
  return itemAdminRequest({
    url: "/vue/y9form/item/bindList",
    method: 'get',
    params: params
  });
}

//获取表单列表
export function getFormList(itemId,procDefId,taskDefKey,systemName){
  const params = {
    itemId:itemId,
    processDefinitionId:procDefId,
    taskDefKey:taskDefKey,
    systemName:systemName
  };
  return itemAdminRequest({
    url: "/vue/y9form/item/formList",
    method: 'get',
    params: params
  });
}

//获取表单列表
export function getY9FormList(systemName){
  const params = {
    systemName:systemName
  };
  return itemAdminRequest({
    url: "/vue/y9form/item/getformList",
    method: 'get',
    params: params
  });
}

//获取绑定表单信息
export function getBindForm(id,procDefId){
  const params = {
    id:id,
    procDefId:procDefId
  };
  return itemAdminRequest({
    url: "/vue/y9form/item/getBindForm",
    method: 'get',
    params: params
  });
}

//保存表单绑定信息
export function saveBindForm(bind){
  const data = qs.stringify(bind);
  return itemAdminRequest({
    url: "/vue/y9form/item/saveBind",
    method: 'post',
    data: data
  });
}

//保存表单绑定信息
export function saveMobileBind(bind){
  const data = qs.stringify(bind);
  return itemAdminRequest({
    url: "/vue/y9form/item/saveMobileBind",
    method: 'post',
    data: data
  });
}

//删除表单绑定
export function deleteBind(id){
  const params = {
    id:id
  };
  return itemAdminRequest({
    url: "/vue/y9form/item/deleteBind",
    method: 'post',
    params: params
  });
}

//删除手机端表单绑定
export function deleteMobileBind(id){
  const params = {
    id:id
  };
  return itemAdminRequest({
    url: "/vue/y9form/item/deleteMobileBind",
    method: 'post',
    params: params
  });
}

//复制上一版本表单
export function copyForm(itemId,processDefinitionId){
  const params = {
    itemId:itemId,
    processDefinitionId:processDefinitionId
  };
  return itemAdminRequest({
    url: "/vue/y9form/item/copyForm",
    method: 'post',
    params: params
  });
}
