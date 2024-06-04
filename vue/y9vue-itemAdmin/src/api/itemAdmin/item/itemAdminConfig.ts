/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2022-07-12 11:47:56
 * @FilePath: \workspace-y9boot-9.5-vuee:\workspace-y9boot-9.6-vue\y9vue-itemAdmin\src\api\itemAdmin\item\itemAdminConfig.ts
 */

import Request from "@/api/lib/request";
var itemAdminRequest = new Request();

//获取流程定义版本
export function getProcessDefinitionList(processDefineKey){
  const params = {
    processDefineKey:processDefineKey
  };
  return itemAdminRequest({
    url: "/vue/itemProcessDefinition/getProcessDefinitionList",
    method: 'get',
    params: params
  });
}