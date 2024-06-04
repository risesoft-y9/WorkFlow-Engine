import Request from '@/api/lib/request';

var flowableRequest = new Request();
export function getAllCountItems(itemId) {
  const params = {
    itemId
  };
  return flowableRequest({
    url: '/vue/mian/getCount4Item',
    method:'get',
    params
  })
}

//获取待办列表视图配置
export function todoViewConf(itemId){
  const params = {
    itemId:itemId
  };
  return flowableRequest({
    url: "/vue/workList/todoViewConf",
    method: 'get',
    params: params
  });
}

//获取待办列表
export function getTodoList(itemId,searchTerm,page,rows){
  const params = {
    itemId:itemId,
    searchTerm:searchTerm,
    page:page,
    rows:rows
  };
  return flowableRequest({
    url: "/vue/workList/todoList",
    method: 'get',
    params: params
  });
}

//获取待办列表
export function searchTodoList(itemId,tableName,searchMapStr,page,rows){
  const params = {
    itemId:itemId,
    tableName:tableName,
    searchMapStr:searchMapStr,
    page:page,
    rows:rows
  };
  return flowableRequest({
    url: "/vue/workList/searchTodoList",
    method: 'get',
    params: params
  });
}

//获取在办列表视图配置
export function doingViewConf(itemId){
  const params = {
    itemId:itemId
  };
  return flowableRequest({
    url: "/vue/workList/doingViewConf",
    method: 'get',
    params: params
  });
}

//获取在办列表
export function getDoingList(itemId,searchTerm,page,rows){
  const params = {
    itemId:itemId,
    searchTerm:searchTerm,
    page:page,
    rows:rows
  };
  return flowableRequest({
    url: "/vue/workList/doingList",
    method: 'get',
    params: params
  });
}

//获取在办列表
export function searchDoingList(itemId,tableName,searchMapStr,page,rows){
  const params = {
    itemId:itemId,
    tableName:tableName,
    searchMapStr:searchMapStr,
    page:page,
    rows:rows
  };
  return flowableRequest({
    url: "/vue/workList/searchDoingList",
    method: 'get',
    params: params
  });
}

//获取办结列表视图配置
export function doneViewConf(itemId){
  const params = {
    itemId:itemId
  };
  return flowableRequest({
    url: "/vue/workList/doneViewConf",
    method: 'get',
    params: params
  });
}

//获取办结列表
export function getDoneList(itemId,searchTerm,page,rows){
  const params = {
    itemId:itemId,
    searchTerm:searchTerm,
    page:page,
    rows:rows
  };
  return flowableRequest({
    url: "/vue/workList/doneList",
    method: 'get',
    params: params
  });
}

//获取办结列表
export function searchDoneList(itemId,tableName,searchMapStr,page,rows){
  const params = {
    itemId:itemId,
    tableName:tableName,
    searchMapStr:searchMapStr,
    page:page,
    rows:rows
  };
  return flowableRequest({
    url: "/vue/workList/searchDoneList",
    method: 'get',
    params: params
  });
}

/**
 * 
 * @param itemId 获取视图
 * @param viewType 
 * @returns 
 */
export function viewConf(itemId,viewType){
  const params = {
    itemId,
    viewType
  };
  return flowableRequest({
    url: "/vue/workList/viewConf",
    method: 'get',
    params: params
  });
}

/**
 * 
 * @param itemId 获取综合查询列表
 * @param state 
 * @param createDate 
 * @param tableName 
 * @param searchMapStr 
 * @param page 
 * @param rows 
 * @returns 
 */
export function getQueryList(itemId,state,createDate,tableName,searchMapStr,page,rows){
  const params = {
    itemId,
    state,
    createDate,
    tableName,
    searchMapStr,
    page,
    rows
  };
  return flowableRequest({
    url: "/vue/workList/queryList",
    method: 'get',
    params: params
  });
}