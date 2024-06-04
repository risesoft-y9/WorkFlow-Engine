/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2024-05-14 10:56:04
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-vue\y9vue-itemAdmin\src\api\itemAdmin\linkInfo.ts
 */

import Request from "@/api/lib/request";
import qs from "qs";

var itemAdminRequest = new Request();
//获取链接列表
export function getLinkList(linkName,linkUrl){
  const params = {
    linkName,
    linkUrl
  };
  return itemAdminRequest({
    url: "/vue/linkInfo/findAll",
    method: 'get',
    params: params
  });
}

//获取链接绑定事项列表
export function findByLinkId(id){
  const params = {
    id
  };
  return itemAdminRequest({
    url: "/vue/linkInfo/findByLinkId",
    method: 'get',
    params: params
  });
}

//保存链接
export function saveOrUpdate(info){
  const data = qs.stringify(info);
  return itemAdminRequest({
    url: "/vue/linkInfo/saveOrUpdate",
    method: 'post',
    data: data
  });
}

//删除链接
export function removeLink(id){
  const params = {
    id
  };
  return itemAdminRequest({
    url: "/vue/linkInfo/remove",
    method: 'post',
    params: params
  });
}

