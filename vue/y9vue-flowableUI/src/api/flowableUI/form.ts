/*
 * @Author: your name
 * @Date: 2021-05-19 09:41:06
 * @LastEditTime: 2026-01-07 10:15:39
 * @LastEditors: mengjuhua
 * @Description: In User Settings Edit
 * @FilePath: \vue\y9vue-flowableUI\src\api\flowableUI\form.ts
 */
import Request from '@/api/lib/request';

var flowableRequest = new Request();
//获取表单初始化数据
export function getFormInitData(url, processSerialNumber) {
    const params = {
        processSerialNumber: processSerialNumber
    };
    if (url == "") {
        url = "/vue/y9form/getInitData";
    }
    return flowableRequest({
        url: url,
        method: 'get',
        params: params
    });
}

//获取表单json数据
export function getFormJson(formId) {
    const params = {
        formId: formId
    };
    return flowableRequest({
        url: "/vue/y9form/getFormJson",
        method: 'get',
        params: params
    });
}


//获取表单数据
export function getFormData(formId, processSerialNumber) {
    const params = {
        formId: formId,
        processSerialNumber: processSerialNumber
    };
    return flowableRequest({
        url: "/vue/y9form/getFormData",
        method: 'get',
        params: params
    });
}



//保存表单数据
export function saveFormData(formId, jsonData) {
    const params = {
        formId: formId,
        jsonData: jsonData
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



//获取表单字段权限
export function getFieldPerm(formId, fieldName, taskDefKey, processDefinitionId) {
    const params = {
        formId: formId,
        fieldName: fieldName,
        taskDefKey: taskDefKey,
        processDefinitionId: processDefinitionId
    };
    return flowableRequest({
        url: "/vue/y9form/getFieldPerm",
        method: 'get',
        params: params
    });
}

//获取表单所有字段权限
export function getAllFieldPerm(formId, taskDefKey, processDefinitionId) {
    const params = {
        formId: formId,
        taskDefKey: taskDefKey,
        processDefinitionId: processDefinitionId
    };
    return flowableRequest({
        url: "/vue/y9form/getAllFieldPerm",
        method: 'get',
        params: params
    });
}

//获取数据字典值
export function getOptionValueList(type) {
    const params = {
        type: type
    };
    return flowableRequest({
        url: "/vue/y9form/getOptionValueList",
        method: 'get',
        params: params
    });
}

/**
 * 有生云请假办件计算假期天数和小时
 * @param {*} params 
 * @returns 
 */
export function getDayOrHour(params) {
    return flowableRequest({
        url: "/vue/sign/getDayOrHour",
        method: 'get',
        params: params
    });
}

export function getCommonDayOrHour(params) {
    return flowableRequest({
        url: "/vue/sign/getCommonDayOrHour",
        method: 'get',
        params: params
    });
}

//获取表单查询字段信息
export function getFormField(itemId) {
    const params = {
        itemId
    };
    return flowableRequest({
        url: "/vue/y9form/getFormField",
        method: 'get',
        params: params
    });
}

//根据formId获取表单字段
export function getFormFieldByFormId(formId) {
    const params = {
        formId
    };
    return flowableRequest({
        url: "/vue/y9form/getFormFieldByFormId",
        method: 'get',
        params: params
    });
}




