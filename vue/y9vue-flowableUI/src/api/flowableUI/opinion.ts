import Request from '@/api/lib/request';

var flowableRequest = new Request();
//获取个人常用语
export function commonSentencesList(){
  const params = {};
  return flowableRequest({
    url:'/vue/commonSentences/list',
    method: 'get',
    params: params
  });
}

//保存个人常用语
export function saveCommonSentences(content){
  const params = {
    content:content
  };
  return flowableRequest({
    url:'/vue/commonSentences/save',
    method: 'POST',
    params: params
  });
}

/**
 * 更新常用语使用次数
 * @param id 
 * @returns 
 */
export function updateUseNumber(id){
  const params = {
    id
  };
  return flowableRequest({
    url:'/vue/commonSentences/updateUseNumber',
    method: 'POST',
    params: params
  });
}

//修改个人常用语
export function editCommonSentences(content,tabIndex){
  const params = {
    content:content,
    tabIndex:tabIndex
  };
  return flowableRequest({
    url:'/vue/commonSentences/saveEdit',
    method: 'POST',
    params: params
  });
}

//删除个人常用语
export function delCommonSentences(tabIndex){
  const params = {
    tabIndex:tabIndex
  };
  return flowableRequest({
    url:'/vue/commonSentences/remove',
    method: 'POST',
    params: params
  });
}

//获取新增或编辑意见前数据
export function personalComment(id){
  const params = {
    id:id
  };
  return flowableRequest({
    url:'/vue/opinion/newOrModify/personalComment',
    method: 'get',
    params: params
  });
}

//获取意见框绑定列表
export function getBindOpinionFrame(itemId,processDefinitionId){
  const params = {
    itemId:itemId,
    processDefinitionId:processDefinitionId
  };
  return flowableRequest({
    url:'/vue/opinion/getBindOpinionFrame',
    method: 'get',
    params: params
  });
}

//获取意见列表
export function getOpinionList(processSerialNumber,taskId,itembox,opinionFrameMark,itemId,taskDefinitionKey,activitiUser){
  const params = {
    processSerialNumber:processSerialNumber,
    taskId:taskId,
    itembox:itembox,
    opinionFrameMark:opinionFrameMark,
    itemId:itemId,
    taskDefinitionKey:taskDefinitionKey,
    activitiUser:activitiUser
  };
  return flowableRequest({
    url:'/vue/opinion/personCommentList',
    method: 'get',
    params: params
  });
}

//保存意见
export function saveOpinion(jsonData){
  const params = {
    jsonData:jsonData
  };
  return flowableRequest({
    url:'/vue/opinion/saveOrUpdate',
    method: 'post',
    params: params
  });
}

//删除意见
export function delOpinion(id){
  const params = {
    id:id
  };
  return flowableRequest({
    url:'/vue/opinion/delete',
    method: 'post',
    params: params
  });
}


//获取委办局树
export function getBureauTree(id){
  const params = {
    id:id
  };
  return flowableRequest({
    url:'/vue/opinion/getBureauTree',
    method: 'get',
    params: params
  });
}

export function getBureauTreeById(param){
  const params = {
    id:param.parentId
  };
  return flowableRequest({
    url:'/vue/opinion/getBureauTree',
    method: 'get',
    params: params
  });
}

//委办局树搜索
export function bureauTreeSearch(param){
  const params = {
    name:param.key
  };
  return flowableRequest({
    url:'/vue/opinion/bureauTreeSearch',
    method: 'get',
    params: params
  });
}

//获取意见框历史记录数量
export function countOpinionHistory(processSerialNumber,opinionFrameMark){
  const params = {
    processSerialNumber:processSerialNumber,
    opinionFrameMark:opinionFrameMark
  };
  return flowableRequest({
    url:'/vue/opinion/countOpinionHistory',
    method: 'get',
    params: params
  });
}

//获取意见框历史记录
export function getOpinionHistoryList(processSerialNumber,opinionFrameMark){
  const params = {
    processSerialNumber:processSerialNumber,
    opinionFrameMark:opinionFrameMark
  };
  return flowableRequest({
    url:'/vue/opinion/opinionHistoryList',
    method: 'get',
    params: params
  });
}
