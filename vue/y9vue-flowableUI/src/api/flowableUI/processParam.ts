/*
 * @Author: your name
 * @Date: 2021-05-19 09:41:06
 * @LastEditTime: 2022-03-02 15:51:02
 * @LastEditors: zhangchongjie
 * @Description: In User Settings Edit
 * @FilePath: \workspace-y9boot-9.5-vue\y9vue-flowableUI\src\api\flowableUI\processParam.js
 */
import Request from '@/api/lib/request';

var flowableRequest = new Request();
//保存自定义变量数据
export function saveProcessParam(itemId,processSerialNumber,processInstanceId,documentTitle,number,level,customItem){
  const params = {
    processSerialNumber:processSerialNumber,
    itemId:itemId,
    processInstanceId:processInstanceId,
    documentTitle:documentTitle,
    number:number,
    level:level,
    customItem:customItem
  };
  return flowableRequest({
    url:'/vue/processParam/saveOrUpdate',
    method: 'POST',
    params: params
  });
}
