/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-10-08 16:18:43
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2021-10-22 11:28:12
 * @FilePath: \workspace-y9boot-9.5-vue\y9vue-flowableUI\src\api\flowableUI\search.js
 */
import Request from '@/api/lib/request';

var flowableRequest = new Request();
//获取个人所有件列表
export function getSearchList(searchName,itemId,userName,state,year,page,rows){
  const params = {
    searchName:searchName,
    itemId:itemId,
    userName:userName,
    state:state,
    year:year,
    page:page,
    rows:rows
  };
  return flowableRequest({
    url: "/vue/search/getSearchList",
    method: 'get',
    params: params
  });
}

//获取阅件列表
export function getYuejianList(searchName,itemId,userName,state,year,page,rows){
  const params = {
    searchName:searchName,
    itemId:itemId,
    userName:userName,
    state:state,
    year:year,
    page:page,
    rows:rows
  };
  return flowableRequest({
    url: "/vue/search/getYuejianList",
    method: 'get',
    params: params
  });
}

//获取我的事项
export function getMyItemList(){
  const params = {};
  return flowableRequest({
    url: "/vue/search/getMyItemList",
    method: 'get',
    params: params
  });
}


//获取公务邮件列表
export function getEmailList(title,userName,fileType,startDate,endDate,page,rows){
  const params = {
    title:title,
    userName:userName,
    fileType:fileType,
    startDate:startDate,
    endDate:endDate,
    page:page,
    rows:rows
  };
  return flowableRequest({
    url: "/vue/search/getEmailList",
    method: 'get',
    params: params
  });
}
