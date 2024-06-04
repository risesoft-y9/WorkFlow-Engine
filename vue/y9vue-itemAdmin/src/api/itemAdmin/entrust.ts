/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2021-08-10 09:50:11
 * @FilePath: \workspace-y9boot-9.5.x-vue\y9vue-itemAdmin\src\api\itemAdmin\entrust.js
 */

import Request from "@/api/lib/request";
import qs from "qs";

var itemAdminRequest = new Request();
//获取委托列表
export function entrustList(){
  const params = {};
  return itemAdminRequest({
    url: "/vue/entrust/list",
    method: 'get',
    params: params
  });
}

//获取委托信息
export function getEntrustInfo(id){
  const params = {
    id:id
  };
  return itemAdminRequest({
    url: "/vue/entrust/getEntrustInfo",
    method: 'get',
    params: params
  });
}

//保存委托信息
export function saveOrUpdate(entrust){
  const data = qs.stringify(entrust);
  return itemAdminRequest({
    url: "/vue/entrust/saveOrUpdate",
    method: 'post',
    data: data
  });
}

//删除委托
export function removeEntrust(id){
  const params = {
    id:id
  };
  return itemAdminRequest({
    url: "/vue/entrust/removeEntrust",
    method: 'post',
    params: params
  });
}

//获取部门树
export function getDeptTree(){
  const params = {};
  return itemAdminRequest({
    url: "/vue/entrust/getDeptTree",
    method: 'get',
    params: params
  });
}

//获取部门树
export function getDeptChildTree(id){
    const params = {
      id:id
    };
    return itemAdminRequest({
      url: "/vue/entrust/getDeptChildTree",
      method: 'get',
      params: params
    });
}



//部门树搜索
export function deptTreeSearch(params){
   
    return itemAdminRequest({
      url: "/vue/entrust/deptTreeSearch",
      method: 'get',
      params: {name:params.name}
    });
}