/*
 * @Author: your name
 * @Date: 2021-05-10 09:48:00
 * @LastEditTime: 2021-05-27 15:45:40
 * @LastEditors: your name
 * @Description: In User Settings Edit
 * @FilePath: \workspace-y9boot-v9.5.x-vue\y9vue-flowableUI\src\api\flowableUI\speakInfo.js
 */
import Request from '@/api/lib/request';

var flowableRequest = new Request();
//获取沟通交流信息
export function speakInfoList(processInstanceId){
  const params = {
    processInstanceId:processInstanceId
  };
  return flowableRequest({
    url:'/vue/speakInfo/speakInfoList',
    method: 'get',
    params: params
  });
}

//保存沟通交流信息
export function saveSpeakInfo(processInstanceId,content){
  const params = {
    processInstanceId:processInstanceId,
    content:content
  };
  return flowableRequest({
    url:'/vue/speakInfo/saveOrUpdate',
    method: 'post',
    params: params
  });
}

//删除沟通交流信息
export function delSpeakInfo(id){
  const params = {
    id:id
  };
  return flowableRequest({
    url:'/vue/speakInfo/deleteById',
    method: 'post',
    params: params
  });
}
