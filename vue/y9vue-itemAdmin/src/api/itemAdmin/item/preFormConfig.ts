/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2024-05-10 14:34:53
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-vue\y9vue-itemAdmin\src\api\itemAdmin\item\preFormConfig.ts
 */

import Request from "@/api/lib/request";
import qs from "qs";

var itemAdminRequest = new Request();
//获取绑定前置表单
export function getBindList(itemId){
  const params = {
    itemId:itemId
  };
  return itemAdminRequest({
    url: "/vue/preFormBind/getBindList",
    method: 'get',
    params: params
  });
}

//删除绑定前置表单
export function deleteBindForm(id){
  const params = {
    id:id
  };
  return itemAdminRequest({
    url: "/vue/preFormBind/deleteBind",
    method: 'post',
    params: params
  });
}

//保存绑定前置表单
export function saveBindForm(itemId,formId,formName){
  const params = {
    itemId,
    formId,
    formName
  };
  return itemAdminRequest({
    url: "/vue/preFormBind/saveBindForm",
    method: 'post',
    params: params
  });
}

//获取表单列表
export function getY9FormList(itemId,systemName,formName){
  const params = {
    itemId,
    systemName,
    formName
  };
  return itemAdminRequest({
    url: "/vue/preFormBind/getFormList",
    method: 'get',
    params: params
  });
}