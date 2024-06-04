import Request from '@/api/lib/request';

var flowableRequest = new Request();
//获取抄送列表
export function search(documentTitle,year,status,page,rows){
  const params = {
    documentTitle:documentTitle,
    year:year,
    status:status,
    page:page,
    rows:rows
  };
  return flowableRequest({
    url: "/vue/chaoSong/search",
    method: 'get',
    params: params
  });
}

//批量已阅
export function changeStatus(ids){
  const params = {
    ids:ids
  };
  return flowableRequest({
    url: "/vue/chaoSong/changeStatus",
    method: 'post',
    params: params
  });
}

//改变抄送件意见状态
export function changeChaoSongState(id,type){
  const params = {
    id:id,
    type:type
  };
  return flowableRequest({
    url: "/vue/chaoSong/changeChaoSongState",
    method: 'post',
    params: params
  });
}

//获取打开抄送件数据
export function chaoSongData(id,processInstanceId,itemId,status){
  const params = {
    id:id,
    processInstanceId:processInstanceId,
    itemId:itemId,
    status:status
  };
  return flowableRequest({
    url: "/vue/chaoSong/detail",
    method: 'get',
    params: params
  });
}

//抄送
export function chaoSongSave(processInstanceId,itemId,processSerialNumber,processDefinitionKey,users,isSendSms,isShuMing,smsContent,smsPersonId){
  const params = {
    processInstanceId:processInstanceId,
    itemId:itemId,
    processSerialNumber:processSerialNumber,
    processDefinitionKey:processDefinitionKey,
    users:users,
    isSendSms:isSendSms,
    isShuMing:isShuMing,
    smsContent:smsContent,
    smsPersonId:smsPersonId
  };
  return flowableRequest({
    url: "/vue/chaoSong/save",
    method: 'post',
    params: params
  });
}

//获取流程实例抄送列表
export function getChaoSongList(type,userName,processInstanceId,page,rows){
  const params = {
    type:type,
    userName:userName,
    processInstanceId:processInstanceId,
    page:page,
    rows:rows
  };
  return flowableRequest({
    url: "/vue/chaoSong/list",
    method: 'get',
    params: params
  });
}

//收回抄送件
export function deleteList(ids){
  const params = {
    ids:ids
  };
  return flowableRequest({
    url: "/vue/chaoSong/deleteList",
    method: 'post',
    params: params
  });
}