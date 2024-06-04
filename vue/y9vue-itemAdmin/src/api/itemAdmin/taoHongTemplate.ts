/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2021-06-22 14:43:07
 * @FilePath: \workspace-y9boot-9.5.x-vue\y9vue-itemAdmin\src\api\itemAdmin\taoHongTemplate.js
 */

import Request from "@/api/lib/request";
import qs from "qs";

var itemAdminRequest = new Request();
//获取套红模板列表
export function getTemplateList(name){
  const params = {
    name:name
  };
  return itemAdminRequest({
    url: "/vue/taoHongTemplate/getList",
    method: 'get',
    params: params
  });
}

//获取套红模板信息
export function getTemplateInfo(id){
  const params = {
    id:id
  };
  return itemAdminRequest({
    url: "/vue/taoHongTemplate/newOrModify",
    method: 'get',
    params: params
  });
}

//获取委办局树
export function bureauTree(name){
  const params = {
    name:name
  };
  return itemAdminRequest({
    url: "/vue/taoHongTemplate/bureauTree",
    method: 'get',
    params: params
  });
}


//保存套红模板信息
export function saveOrUpdate(taoHongInfo,file){
  let formData = new FormData();
  formData.append("file", file);
  formData.append("template_guid", taoHongInfo.template_guid);
  formData.append("bureau_guid", taoHongInfo.bureau_guid);
  formData.append("bureau_name", taoHongInfo.bureau_name);
  formData.append("template_type", taoHongInfo.template_type);
  return itemAdminRequest({
    url: "/vue/taoHongTemplate/saveOrUpdate",
    method: 'post',
    data: formData
  });
}

//删除套红模板
export function removeTaoHongTemplate(ids){
  const params = {
    ids:ids
  };
  return itemAdminRequest({
    url: "/vue/taoHongTemplate/removeTaoHongTemplate",
    method: 'post',
    params: params
  });
}

//获取模板类型列表
export function getTemplateTypeList(){
  const params = {};
  return itemAdminRequest({
    url: "/vue/taoHongTemplateType/list",
    method: 'get',
    params: params
  });
}

//获取模板类型
export function getTemplateType(id){
  const params = {
    id:id
  };
  return itemAdminRequest({
    url: "/vue/taoHongTemplateType/newOrModify",
    method: 'get',
    params: params
  });
}

//保存模板类型
export function saveTemplateType(type){
  const data = qs.stringify(type);
  return itemAdminRequest({
    url: "/vue/taoHongTemplateType/saveOrUpdate",
    method: 'post',
    data: data
  });
}

//删除模板类型
export function removeTemplateType(ids){
  const params = {
    ids:ids
  };
  return itemAdminRequest({
    url: "/vue/taoHongTemplateType/removeTaoHongTemplateType",
    method: 'post',
    params: params
  });
}

//保存模板类型排序
export function saveOrder(idAndTabIndexs){
  const params = {
    idAndTabIndexs:idAndTabIndexs
  };
  return itemAdminRequest({
    url: "/vue/taoHongTemplateType/saveOrder",
    method: 'post',
    params: params
  });
}