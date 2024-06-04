/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2021-06-18 11:16:33
 * @FilePath: \workspace-y9boot-9.5.x-vue\y9vue-itemAdmin\src\api\itemAdmin\dynamicRole.js
 */

import Request from "@/api/lib/request";
import qs from "qs";

var itemAdminRequest = new Request();
//获取动态角色列表
export function dynamicRoleList(){
  const params = {};
  return itemAdminRequest({
    url: "/vue/dynamicRole/dynamicRoleList",
    method: 'get',
    params: params
  });
}

//获取动态角色
export function getDynamicRole(id){
  const params = {
    id:id
  };
  return itemAdminRequest({
    url: "/vue/dynamicRole/getDynamicRole",
    method: 'get',
    params: params
  });
}

//保存动态角色
export function saveOrUpdate(dynamicRole){
  const data = qs.stringify(dynamicRole);
  return itemAdminRequest({
    url: "/vue/dynamicRole/saveOrUpdate",
    method: 'post',
    data: data
  });
}

//删除动态角色
export function removeDynamicRole(dynamicRoleIds){
  const params = {
    dynamicRoleIds:dynamicRoleIds
  };
  return itemAdminRequest({
    url: "/vue/dynamicRole/remove",
    method: 'post',
    params: params
  });
}

