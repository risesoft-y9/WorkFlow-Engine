/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2021-08-02 18:16:48
 * @FilePath: \workspace-y9boot-9.5.x-vue\y9vue-itemAdmin\src\api\itemAdmin\item\wordConfig.js
 */

import Request from "@/api/lib/request";

var itemAdminRequest = new Request();
//获取绑定信息
export function getTemplateBind(itemId){
  const params = {
    itemId:itemId
  };
  return itemAdminRequest({
    url: "/vue/itemWordBind/getTemplateBind",
    method: 'get',
    params: params
  });
}

//删除模板
export function deleteBind(id){
  const params = {
    id:id
  };
  return itemAdminRequest({
    url: "/vue/itemWordBind/deleteBind",
    method: 'post',
    params: params
  });
}

//保存绑定模板
export function saveBind(itemId,templateId,processDefinitionId){
  const params = {
    itemId:itemId,
    templateId:templateId,
    processDefinitionId:processDefinitionId
  };
  return itemAdminRequest({
    url: "/vue/itemWordBind/save",
    method: 'post',
    params: params
  });
}
