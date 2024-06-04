import Request from '@/api/lib/request';

var flowableRequest = new Request();
//获取简易历程
export function processList(processInstanceId){
  const params = {
    processInstanceId:processInstanceId
  };
  return flowableRequest({
    url:'/vue/processTrack/processList',
    method: 'get',
    params: params
  });
}

//获取历程信息
export function historyList(processInstanceId){
  const params = {
    processInstanceId:processInstanceId
  };
  return flowableRequest({
    url:'/vue/processTrack/historyList',
    method: 'get',
    params: params
  });
}
