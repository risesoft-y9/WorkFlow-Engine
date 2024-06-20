/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2023-11-15 11:50:14
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-vue\y9vue-itemAdmin\src\api\itemAdmin\y9form_fieldPerm.ts
 */

import Request from "@/api/lib/request";
import qs from "qs";
var itemAdminRequest = new Request();
//获取字段权限配置列表
export function getBpmList(formId,fieldName){
  const params = {
    formId:formId,
    fieldName:fieldName
  };
  return itemAdminRequest({
    url: "/vue/y9form/fieldPerm/getBpmList",
    method: 'get',
    params: params
  });
}

//保存字段角色权限
export function saveRoleChoice(formId,fieldName,taskDefKey,roleNames,roleIds){
  let formData = new FormData();
  formData.append("formId", formId);
  formData.append("fieldName", fieldName);
  formData.append("taskDefKey", taskDefKey);
  formData.append("roleNames", roleNames);
  formData.append("roleIds", roleIds);
  return itemAdminRequest({
    url: "/vue/y9form/fieldPerm/saveRoleChoice",
    method: 'post',
    data: formData
  });
}

//保存节点权限
export function saveNodePerm(formId,fieldName,taskDefKey){
  let formData = new FormData();
  formData.append("formId", formId);
  formData.append("fieldName", fieldName);
  formData.append("taskDefKey", taskDefKey);
  return itemAdminRequest({
    url: "/vue/y9form/fieldPerm/saveNodePerm",
    method: 'post',
    data: formData
  });
}

//删除字段角色权限
export function deleteRole(formId,fieldName,taskDefKey){
  let formData = new FormData();
  formData.append("formId", formId);
  formData.append("fieldName", fieldName);
  formData.append("taskDefKey", taskDefKey);
  return itemAdminRequest({
    url: "/vue/y9form/fieldPerm/deleteRole",
    method: 'post',
    data: formData
  });
}

//删除字段权限
export function delNodePerm(formId,fieldName,taskDefKey){
  let formData = new FormData();
  formData.append("formId", formId);
  formData.append("fieldName", fieldName);
  formData.append("taskDefKey", taskDefKey);
  return itemAdminRequest({
    url: "/vue/y9form/fieldPerm/delNodePerm",
    method: 'post',
    data: formData
  });
}

//获取该字段是否配置权限
export function countPerm(formId,fieldName){
  const params = {
    formId:formId,
    fieldName:fieldName
  };
  return itemAdminRequest({
    url: "/vue/y9form/fieldPerm/countPerm",
    method: 'get',
    params: params
  });
}

//获取表单所有字段权限
export function getAllPerm(formId){
  const params = {
    formId
  };
  return itemAdminRequest({
    url: "/vue/y9form/fieldPerm/getAllPerm",
    method: 'get',
    params: params
  });
}