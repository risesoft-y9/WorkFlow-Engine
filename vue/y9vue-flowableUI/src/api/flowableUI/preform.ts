/*
 * @Author: your name
 * @Date: 2021-05-19 09:41:06
 * @LastEditTime: 2024-04-03 18:11:47
 * @LastEditors: zhangchongjie
 * @Description: In User Settings Edit
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-vue\y9vue-flowableUI\src\api\flowableUI\form.ts
 */
import Request from '@/api/lib/request';

var flowableRequest = new Request();

/**
 * 获取事项绑定的前置表单
 * @param itemId 
 * @returns 
 */
export function getBindPreFormByItemId(itemId){
  const params = {
    itemId
  };
  return flowableRequest({
    url: "/vue/y9form/getBindPreFormByItemId",
    method: 'get',
    params: params
  });
}

//保存表单数据
export function savePreFormData(itemId,formId,jsonData){
  let formData = new FormData();
  formData.append("itemId", itemId);
  formData.append("formId", formId);
  formData.append("jsonData", jsonData);
  return flowableRequest({
    url: "/vue/y9form/savePreFormData",
    method: 'post',
    data: formData
  });
}
