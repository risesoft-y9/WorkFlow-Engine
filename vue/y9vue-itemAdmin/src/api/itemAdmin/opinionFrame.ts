/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2021-09-23 17:57:01
 * @FilePath: \workspace-y9boot-9.5.x-vue\y9vue-itemAdmin\src\api\itemAdmin\opinionFrame.js
 */

import Request from "@/api/lib/request";
import qs from "qs";

var itemAdminRequest = new Request();
//获取意见框列表
export function opinionFrameList(page,rows){
  const params = {
    page:page,
    rows:rows
  };
  return itemAdminRequest({
    url: "/vue/opinionFrame/list",
    method: 'get',
    params: params
  });
}

//获取意见框
export function getOpinionFrame(id){
  const params = {
    id:id
  };
  return itemAdminRequest({
    url: "/vue/opinionFrame/getOpinionFrame",
    method: 'get',
    params: params
  });
}

//保存意见框
export function saveOrUpdate(opinionFrame){
  const data = qs.stringify(opinionFrame);
  return itemAdminRequest({
    url: "/vue/opinionFrame/saveOrUpdate",
    method: 'post',
    data: data
  });
}

//删除意见框
export function removeOpinionFrame(ids){
  const params = {
    ids:ids
  };
  return itemAdminRequest({
    url: "/vue/opinionFrame/remove",
    method: 'post',
    params: params
  });
}

//搜索意见框
export function searchOpinionFrame(page,rows,keyword){
  const params = {
    keyword:keyword,
    page:page,
    rows:rows
  };
  return itemAdminRequest({
    url: "/vue/opinionFrame/search",
    method: 'get',
    params: params
  });
}

//获取意见框授权列表
export function getBindListByMark(mark){
  const params = {
    mark:mark
  };
  return itemAdminRequest({
    url: "/vue/itemOpinionFrameBind/getBindListByMark",
    method: 'get',
    params: params
  });
}

//删除意见框授权列表
export function deleteBind(id){
  const params = {
    ids:id
  };
  return itemAdminRequest({
    url: "/vue/itemOpinionFrameBind/remove",
    method: 'post',
    params: params
  });
}