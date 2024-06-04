/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2021-12-14 11:51:40
 * @FilePath: \workspace-y9boot-9.5-vue\y9vue-itemAdmin\src\api\itemAdmin\item\signConfig.js
 */

import Request from "@/api/lib/request";
import qs from "qs";

var itemAdminRequest = new Request();
//获取签收配置信息
export function getBpmList(processDefinitionId,itemId){
  const params = {
    processDefinitionId:processDefinitionId,
    itemId:itemId
  };
  return itemAdminRequest({
    url: "/vue/itemTaskConfig/getBpmList",
    method: 'get',
    params: params
  });
}

//保存签收配置
export function saveBind(signConfig){
  const data = qs.stringify(signConfig);
  return itemAdminRequest({
    url: "/vue/itemTaskConfig/saveBind",
    method: 'post',
    data: data
  });
}


//复制上一版本签收配置
export function copyBind(itemId,processDefinitionId){
  const params = {
    itemId:itemId,
    processDefinitionId:processDefinitionId
  };
  return itemAdminRequest({
    url: "/vue/itemTaskConfig/copyTaskConfig",
    method: 'post',
    params: params
  });
}