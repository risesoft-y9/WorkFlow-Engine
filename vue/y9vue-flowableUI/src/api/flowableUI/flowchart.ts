/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2024-05-11 16:39:47
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2024-05-11 16:40:49
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-vue\y9vue-flowableUI\src\api\flowableUI\flowchart.ts
 */
import Request from '@/api/lib/request';

var flowableRequest = new Request();

//获取流程图
export function getFlowChart(params){
  return flowableRequest({
    url: "/vue/processTrack/getFlowChart",
    method: 'get',
    params: params
  });
}

//获取流程图任务节点信息
export function getTaskList(processInstanceId){
  const params = {
    processInstanceId
  };
  return flowableRequest({
    url: "/vue/processTrack/getTaskList",
    method: 'get',
    params: params
  });
}

