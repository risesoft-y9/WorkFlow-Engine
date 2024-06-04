/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2021-07-01 10:05:55
 * @FilePath: \workspace-y9boot-9.5.x-vue\y9vue-itemAdmin\src\api\itemAdmin\wordTemplate.js
 */

import Request from "@/api/lib/request";
import qs from "qs";

var itemAdminRequest = new Request();
//获取正文模板列表
export function wordTemplateList(){
  const params = {};
  return itemAdminRequest({
    url: "/vue/wordTemplate/wordTemplateList",
    method: 'get',
    params: params
  });
}

//上传正文模板
export function uploadTemplate(file){
  let formData = new FormData();
  formData.append("file", file);
  return itemAdminRequest({
    url: "/vue/wordTemplate/upload",
    method: 'post',
    data: formData
  });
}


//删除正文模板
export function deleteWordTemplate(id){
  const params = {
    id:id
  };
  return itemAdminRequest({
    url: "/vue/wordTemplate/deleteWordTemplate",
    method: 'post',
    params: params
  });
}

//下载打印模板
export function download(id){
  const params = {id:id};
  return itemAdminRequest({
    url: "/vue/wordTemplate/download",
    method: 'get',
    params: params
  });
}

//获取书签列表
export function bookMarKList(wordTemplateId,wordTemplateType){
  const params = {
    wordTemplateId:wordTemplateId,
    wordTemplateType:wordTemplateType
  };
  return itemAdminRequest({
    url: "/vue/wordTemplate/bookMarKList",
    method: 'get',
    params: params
  });
}

//获取书签绑定信息
export function getBookMarkBind(wordTemplateId,bookMarkName){
  const params = {
    wordTemplateId:wordTemplateId,
    bookMarkName:bookMarkName
  };
  return itemAdminRequest({
    url: "/vue/wordTemplate/getBookMarkBind",
    method: 'get',
    params: params
  });
}

//获取数据库表列
export function getColumns(tableId){
  const params = {
    tableId:tableId
  };
  return itemAdminRequest({
    url: "/vue/wordTemplate/getColumns",
    method: 'get',
    params: params
  });
}

//保存书签绑定信息
export function saveOrUpdate(bookMarkBind){
  const data = qs.stringify(bookMarkBind);
  return itemAdminRequest({
    url: "/vue/bookMarkBind/saveOrUpdate",
    method: 'post',
    data: data
  });
}

//删除书签绑定
export function deleteBind(wordTemplateId,bookMarkName){
  const params = {
    wordTemplateId:wordTemplateId,
    bookMarkName:bookMarkName
  };
  return itemAdminRequest({
    url: "/vue/bookMarkBind/deleteBind",
    method: 'post',
    params: params
  });
}