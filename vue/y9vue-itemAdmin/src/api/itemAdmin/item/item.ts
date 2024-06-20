/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2022-07-05 17:11:02
 * @FilePath: \workspace-y9boot-9.5-vuee:\workspace-y9boot-9.6-vue\y9vue-itemAdmin\src\api\itemAdmin\item\item.ts
 */

import Request from "@/api/lib/request";
import qs from "qs";
var itemAdminRequest = new Request();
//获取事项列表
export function getItemList(){
  const params = {};
  return itemAdminRequest({
    url: "/vue/item/list",
    method: 'get',
    params: params
  });
}

//获取新增或修改事项数据
export function getItemData(id){
  const params = {
    id:id
  };
  return itemAdminRequest({
    url: "/vue/item/newOrModify",
    method: 'get',
    params: params
  });
}

//获取图片icon
export function readAppIconFile(){
  const params = {};
  return itemAdminRequest({
    url: "/vue/item/readAppIconFile",
    method: 'get',
    params: params
  });
}

//搜索icon
export function searchIcon(name){
  const params = {
    name:name
  };
  return itemAdminRequest({
    url: "/vue/item/searchAppIcon",
    method: 'get',
    params: params
  });
}

//保存事项
export function saveItem(item){
  const params = {
    itemJson:item
  };
  const data = qs.stringify(params);
  return itemAdminRequest({
    url: "/vue/item/save",
    method: 'post',
    data: data
  });
}

//删除事项
export function deleteItem(id){
  const params = {
    id:id
  };
  return itemAdminRequest({
    url: "/vue/item/delete",
    method: 'post',
    params: params
  });
}

//发布为应用系统
export function publishToSystemApp(itemId){
  const params = {
    itemId:itemId
  };
  return itemAdminRequest({
    url: "/vue/item/publishToSystemApp",
    method: 'post',
    params: params
  });
}
