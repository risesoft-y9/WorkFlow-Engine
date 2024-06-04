/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2024-05-14 16:31:35
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-vue\y9vue-itemAdmin\src\api\itemAdmin\item\linkInfoConfig.ts
 */

import Request from "@/api/lib/request";
import qs from "qs";

var itemAdminRequest = new Request();
//获取事项链接绑定列表
export function getBindList(itemId){
  const params = {
    itemId
  };
  return itemAdminRequest({
    url: "/vue/itemLinkBind/getBindList",
    method: 'get',
    params: params
  });
}


//删除链接绑定
export function removeBind(ids){
  const params = {
    ids
  };
  return itemAdminRequest({
    url: "/vue/itemLinkBind/removeBind",
    method: 'post',
    params: params
  });
}

//保存链接绑定
export function saveItemLinkBind(itemId,linkIds){
  const params = {
    itemId,
    linkIds
  };
  const data = qs.stringify(params);
  return itemAdminRequest({
    url: "/vue/itemLinkBind/saveItemLinkBind",
    method: 'post',
    data: data
  });
}

//移除角色绑定
export function removeRole(ids){
  const params = {
    ids
  };
  const data = qs.stringify(params);
  return itemAdminRequest({
    url: "/vue/itemLinkBind/removeRole",
    method: 'post',
    data: data
  });
}

//链接绑定角色
export function saveBindRole(itemLinkId,roleIds){
  const params = {
    itemLinkId,
    roleIds
  };
  return itemAdminRequest({
    url: "/vue/itemLinkBind/saveBindRole",
    method: 'post',
    params: params
  });
}

//获取绑定的角色列表
export function getBindRoleList(itemLinkId){
  const params = {
    itemLinkId
  };
  return itemAdminRequest({
    url: "/vue/itemLinkBind/getBindRoleList",
    method: 'get',
    params: params
  });
}

