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
//获取表单初始化数据
export function getFormInitData(url,processSerialNumber){
  const params = {
    processSerialNumber:processSerialNumber
  };
  if(url == ""){
    url = "/vue/y9form/getInitData";
  }
  return flowableRequest({
    url: url,
    method: 'get',
    params: params
  });
}

//获取表单json数据
export function getFormJson(formId){
  const params = {
    formId:formId
  };
  return flowableRequest({
    url: "/vue/y9form/getFormJson",
    method: 'get',
    params: params
  });
}


//获取表单数据
export function getFormData(formId,processSerialNumber){
  const params = {
    formId:formId,
    processSerialNumber:processSerialNumber
  };
  return flowableRequest({
    url: "/vue/y9form/getFormData",
    method: 'get',
    params: params
  });
}

//获取子表数据
export function getChildTableData(formId,tableId,processSerialNumber){
  const params = {
    formId:formId,
    tableId:tableId,
    processSerialNumber:processSerialNumber
  };
  return flowableRequest({
    url: "/vue/y9form/getChildTableData",
    method: 'get',
    params: params
  });
}


//保存表单数据
export function saveFormData(formId,jsonData){
  const params = {
    formId:formId,
    jsonData:jsonData
  };
  let formData = new FormData();
  formData.append("formId", formId);
  formData.append("jsonData", jsonData);
  return flowableRequest({
    url: "/vue/y9form/saveFormData",
    method: 'post',
    data: formData
  });
}

//保存子表数据
export function saveChildTableData(formId,tableId,processSerialNumber,jsonData){
  const params = {
    formId:formId,
    tableId:tableId,
    processSerialNumber:processSerialNumber,
    jsonData:jsonData
  };
  let formData = new FormData();
  formData.append("formId", formId);
  formData.append("jsonData", jsonData);
  formData.append("tableId", tableId);
  formData.append("processSerialNumber", processSerialNumber);
  return flowableRequest({
    url: "/vue/y9form/saveChildTableData",
    method: 'post',
    data: formData
  });
}

//删除子表数据
export function delChildTableRow(formId,tableId,guid){
  const params = {
    formId:formId,
    tableId:tableId,
    guid:guid
  };
  return flowableRequest({
    url: "/vue/y9form/delChildTableRow",
    method: 'post',
    params: params
  });
}


//获取表单字段权限
export function getFieldPerm(formId,fieldName,taskDefKey,processDefinitionId){
  const params = {
    formId:formId,
    fieldName:fieldName,
    taskDefKey:taskDefKey,
    processDefinitionId:processDefinitionId
  };
  return flowableRequest({
    url: "/vue/y9form/getFieldPerm",
    method: 'get',
    params: params
  });
}

//获取表单所有字段权限
export function getAllFieldPerm(formId,taskDefKey,processDefinitionId){
  const params = {
    formId:formId,
    taskDefKey:taskDefKey,
    processDefinitionId:processDefinitionId
  };
  return flowableRequest({
    url: "/vue/y9form/getAllFieldPerm",
    method: 'get',
    params: params
  });
}

//获取数据字典值
export function getOptionValueList(type){
  const params = {
    type:type
  };
  return flowableRequest({
    url: "/vue/y9form/getOptionValueList",
    method: 'get',
    params: params
  });
}

//地灾——针对请假、出差、外勤做考勤记录
export function saveToSign(username,startDate,endDate,type,leaveYear){
  const params = {
    username:username,
    startDate:startDate,
    endDate:endDate,
    type:type,
    leaveYear:leaveYear
  };
  return flowableRequest({
    url: "/vue/sign/saveToSign",
    method: 'post',
    params: params
  });
}

//地灾——计算假期
export function getDay(startDate,endDate){
  const params = {
    startDate:startDate,
    endDate:endDate
  };
  return flowableRequest({
    url: "/vue/sign/getDay",
    method: 'get',
    params: params
  });
}


//地灾——计算年度假期
export function getYearLeave(year){
  const params = {
    year:year
  };
  return flowableRequest({
    url: "/vue/y9form/getYearLeave",
    method: 'get',
    params: params
  });
}

//地灾——留言审批回执
export function lyToReturn(lysp_bianhao,lysp_shifouzhanshi,lysp_huifuneirong){
  const params = {
    lysp_bianhao:lysp_bianhao,
    lysp_shifouzhanshi:lysp_shifouzhanshi,
    lysp_huifuneirong:lysp_huifuneirong
  };
  return flowableRequest({
    url: "/vue/sign/lyToReturn",
    method: 'post',
    params: params
  });
}

/**
 * 有生云请假办件计算假期天数和小时
 * @param {*} params 
 * @returns 
 */
export function getDayOrHour(params){
  return flowableRequest({
    url: "/vue/sign/getDayOrHour",
    method: 'post',
    params: params
  });
}



//获取表单查询字段信息
export function getFormField(itemId){
  const params = {
    itemId
  };
  return flowableRequest({
    url: "/vue/y9form/getFormField",
    method: 'get',
    params: params
  });
}



