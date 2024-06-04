/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2021-09-26 16:41:21
 * @FilePath: \workspace-y9boot-9.5.x-vue\y9vue-itemAdmin\src\api\itemAdmin\commonButton.js
 */

import Request from "@/api/lib/request";
import qs from "qs";

var itemAdminRequest = new Request();
//获取按钮列表
export function getCommonButtonList(){
  const params = {};
  return itemAdminRequest({
    url: "/vue/commonButton/getCommonButtonList",
    method: 'get',
    params: params
  });
}

//获取按钮信息
export function getCommonButton(id){
  const params = {
    id:id
  };
  return itemAdminRequest({
    url: "/vue/commonButton/getCommonButton",
    method: 'get',
    params: params
  });
}

//保存按钮
export function saveOrUpdate(commonButton){
  const data = qs.stringify(commonButton);
  return itemAdminRequest({
    url: "/vue/commonButton/saveOrUpdate",
    method: 'post',
    data: data
  });
}

//删除按钮
export function removeCommonButton(commonButtonIds){
  const params = {
    commonButtonIds:commonButtonIds
  };
  return itemAdminRequest({
    url: "/vue/commonButton/removeCommonButtons",
    method: 'post',
    params: params
  });
}

//校验按钮是否存在
export function checkCustomId(customId){
  const params = {
    customId:customId
  };
  return itemAdminRequest({
    url: "/vue/commonButton/checkCustomId",
    method: 'get',
    params: params
  });
}

//获取按钮授权列表
export function getBindListByButtonId(buttonId){
  const params = {
    buttonId:buttonId
  };
  return itemAdminRequest({
    url: "/vue/itemButtonBind/getBindListByButtonId",
    method: 'get',
    params: params
  });
}

//删除按钮授权列表
export function deleteBind(id){
  const params = {
    ids:id
  };
  return itemAdminRequest({
    url: "/vue/itemButtonBind/removeBind",
    method: 'post',
    params: params
  });
}