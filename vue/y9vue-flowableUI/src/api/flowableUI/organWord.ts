/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-09-28 10:49:27
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2021-09-28 16:15:41
 * @FilePath: \workspace-y9boot-9.5.x-vue\y9vue-flowableUI\src\api\flowableUI\organWord.js
 */
import Request from '@/api/lib/request';

var flowableRequest = new Request();
//获取编号权限及机关代字
export function findByCustom(itemId, processDefinitionId, taskDefKey, custom){
  const params = {
    itemId:itemId,
    processDefinitionId:processDefinitionId,
    taskDefKey:taskDefKey,
    custom:custom
  };
  return flowableRequest({
    url:'/vue/organWord/findByCustom',
    method: 'get',
    params: params
  });
}

//获取默认编号
export function getNumber(itemId,custom,characterValue,year){
  const params = {
    itemId:itemId,
    year:year,
    custom:custom,
    characterValue:characterValue,
    common:2
  };
  return flowableRequest({
    url:'/vue/organWord/getNumber',
    method: 'get',
    params: params
  });
}

//检查编号是否使用
export function checkNumber(itemId,custom,characterValue,year,number,processSerialNumber){
  const params = {
    itemId:itemId,
    processSerialNumber:processSerialNumber,
    year:year,
    custom:custom,
    characterValue:characterValue,
    number:number,
    common:2
  };
  return flowableRequest({
    url:'/vue/organWord/checkNumber',
    method: 'post',
    params: params
  });
}
