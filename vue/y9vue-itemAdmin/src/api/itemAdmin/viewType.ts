/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2021-06-18 11:32:37
 * @FilePath: \workspace-y9boot-9.5.x-vue\y9vue-itemAdmin\src\api\itemAdmin\opinionFrame.js
 */

import Request from "@/api/lib/request";
import qs from "qs";

var itemAdminRequest = new Request();
//获取意见框列表
export function viewTypeList(page,rows){
  const params = {
    page:page,
    rows:rows
  };
  return itemAdminRequest({
    url: "/vue/viewType/list",
    method: 'get',
    params: params
  });
}

//获取意见框
export function getViewType(id){
  const params = {
    id:id
  };
  return itemAdminRequest({
    url: "/vue/viewType/findById",
    method: 'get',
    params: params
  });
}

//保存意见框
export function saveOrUpdate(viewType){
  const data = qs.stringify(viewType);
  return itemAdminRequest({
    url: "/vue/viewType/saveOrUpdate",
    method: 'post',
    data: data
  });
}

//删除意见框
export function removeViewType(ids){
  const params = {
    ids:ids
  };
  return itemAdminRequest({
    url: "/vue/viewType/remove",
    method: 'post',
    params: params
  });
}

