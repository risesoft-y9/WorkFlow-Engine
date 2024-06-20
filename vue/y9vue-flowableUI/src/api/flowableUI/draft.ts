import Request from '@/api/lib/request';

var flowableRequest = new Request();
//获取草稿列表视图配置
export function draftViewConf(itemId){
  const params = {
    itemId:itemId
  };
  return flowableRequest({
    url: "/vue/draft/draftViewConf",
    method: 'get',
    params: params
  });
}

//获取草稿列表
export function getDraftList(itemId,searchTerm,page,rows){
  const params = {
    itemId:itemId,
    title:searchTerm,
    page:page,
    rows:rows
  };
  return flowableRequest({
    url: "/vue/draft/draftList",
    method: 'get',
    params: params
  });
}

//获取回收站列表
export function getDraftRecycleList(itemId,searchTerm,page,rows){
  const params = {
    itemId:itemId,
    title:searchTerm,
    page:page,
    rows:rows
  };
  return flowableRequest({
    url: "/vue/draft/draftRecycleList",
    method: 'get',
    params: params
  });
}

//删除草稿
export function removeDraft(ids){
  const params = {
    ids:ids
  };
  return flowableRequest({
    url: "/vue/draft/removeDraft",
    method: 'post',
    params: params
  });
}

//彻底删除草稿
export function deleteDraft(ids){
  const params = {
    ids:ids
  };
  return flowableRequest({
    url: "/vue/draft/deleteDraft",
    method: 'post',
    params: params
  });
}

//还原草稿
export function reduction(id){
  const params = {
    id:id
  };
  return flowableRequest({
    url: "/vue/draft/reduction",
    method: 'post',
    params: params
  });
}


//获取打开草稿数据
export function openDraft(processSerialNumber,itemId){
  const params = {
    processSerialNumber:processSerialNumber,
    itemId:itemId,
    draftRecycle:'',
  };
  return flowableRequest({
    url:'/vue/draft/openDraft',
    method: 'get',
    params: params
  });
}

//保存草稿
export function saveDraft(processSerialNumber,itemId,processDefinitionKey,number,level,title){
  const params = {
    processSerialNumber:processSerialNumber,
    itemId:itemId,
    processDefinitionKey:processDefinitionKey,
    number:number,
    level:level,
    title:title
  };
  return flowableRequest({
    url:'/vue/draft/saveDraft',
    method: 'post',
    params: params
  });
}


