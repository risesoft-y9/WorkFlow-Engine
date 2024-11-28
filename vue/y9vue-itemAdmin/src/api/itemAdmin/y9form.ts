/*
 * @Descripttion:
 * @version:
 * @Author: zhangchongjie
 * @Date: 2022-07-06 15:47:01
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2024-01-16 15:47:54
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-vue\y9vue-itemAdmin\src\api\itemAdmin\y9form.ts
 */
import Request from '@/api/lib/request';
import qs from 'qs';

var itemAdminRequest = new Request();

//获取系统应用
export function getAppList() {
    const params = {};
    return itemAdminRequest({
        url: '/vue/y9form/table/getAppList',
        method: 'get',
        params: params
    });
}

//获取业务表列表
export function getTables(systemName, page, rows) {
    const params = {
        systemName: systemName,
        page: page,
        rows: rows
    };
    return itemAdminRequest({
        url: '/vue/y9form/table/getTables',
        method: 'get',
        params: params
    });
}

//获取数据库表
export function getAllTables(name) {
    const params = {
        name: name
    };
    return itemAdminRequest({
        url: '/vue/y9form/table/getAllTables',
        method: 'get',
        params: params
    });
}

//添加数据库表
export function addDataBaseTable(tableName, systemName, systemCnName) {
    const params = {
        tableName: tableName,
        systemName: systemName,
        systemCnName: systemCnName
    };
    return itemAdminRequest({
        url: '/vue/y9form/table/addDataBaseTable',
        method: 'post',
        params: params
    });
}

//删除业务表
export function removeTable(id) {
    const params = {
        ids: id
    };
    return itemAdminRequest({
        url: '/vue/y9form/table/removeTable',
        method: 'post',
        params: params
    });
}

//获取业务表信息
export function getTable(id) {
    const params = {
        id: id
    };
    return itemAdminRequest({
        url: '/vue/y9form/table/newOrModifyTable',
        method: 'get',
        params: params
    });
}

//获取表字段列表
export function getTableFieldList(tableId) {
    const params = {
        tableId: tableId
    };
    return itemAdminRequest({
        url: '/vue/y9form/tableField/getTableFieldList',
        method: 'get',
        params: params
    });
}

//获取表字段信息
export function getFieldInfo(id, tableId) {
    const params = {
        id: id,
        tableId: tableId
    };
    return itemAdminRequest({
        url: '/vue/y9form/tableField/newOrModifyField',
        method: 'get',
        params: params
    });
}

//保存表字段信息
export function saveField(field) {
    return itemAdminRequest({
        url: '/vue/y9form/tableField/saveField',
        method: 'post',
        params: field
    });
}

//保存表单信息
export function saveTable(table, fields) {
    let formData = new FormData();
    formData.append('tables', table);
    formData.append('fields', fields);
    return itemAdminRequest({
        url: '/vue/y9form/table/saveTable',
        method: 'post',
        data: formData
    });
}

//新生成表
export function buildTable(table, fields) {
    let formData = new FormData();
    formData.append('tables', table);
    formData.append('fields', fields);
    return itemAdminRequest({
        url: '/vue/y9form/table/buildTable',
        method: 'post',
        data: formData
    });
}

//修改表结构
export function updateTable(table, fields) {
    let formData = new FormData();
    formData.append('tables', table);
    formData.append('fields', fields);
    return itemAdminRequest({
        url: '/vue/y9form/table/updateTable',
        method: 'post',
        data: formData
    });
}

//是否存在数据库表
export function checkTableExist(tableName) {
    const params = {
        tableName: tableName
    };
    return itemAdminRequest({
        url: '/vue/y9form/table/checkTableExist',
        method: 'get',
        params: params
    });
}

//获取表单列表
export function getFormList(systemName, page, rows) {
    const params = {
        systemName: systemName,
        page: page,
        rows: rows
    };
    return itemAdminRequest({
        url: '/vue/y9form/getFormList',
        method: 'get',
        params: params
    });
}

//获取表单
export function getForm(id) {
    const params = {
        id: id
    };
    return itemAdminRequest({
        url: '/vue/y9form/getForm',
        method: 'get',
        params: params
    });
}

//删除表单
export function removeForm(ids) {
    const params = {
        ids: ids
    };
    return itemAdminRequest({
        url: '/vue/y9form/removeForm',
        method: 'post',
        params: params
    });
}

//保存表单
export function newOrModifyForm(data) {
    return itemAdminRequest({
        url: '/vue/y9form/newOrModifyForm',
        method: 'post',
        params: data
    });
}

//保存表单JSON
export function saveFormJson(id, formJson) {
    let formData = new FormData();
    formData.append('id', id);
    formData.append('formJson', formJson);
    // const params = {
    //   id:id,
    //   formJson:formJson
    // };
    // const data = qs.stringify(params);
    return itemAdminRequest({
        url: '/vue/y9form/saveFormJson',
        method: 'post',
        data: formData
    });
}

//保存表单字段绑定信息
export function saveFormField(formId, fieldJson) {
    const params = {
        formId: formId,
        fieldJson: fieldJson
    };
    const data = qs.stringify(params);
    return itemAdminRequest({
        url: '/vue/y9form/saveFormField',
        method: 'post',
        data: data
    });
}

/**
 *
 * @param formId 获取表单绑定字段
 * @param page
 * @param rows
 * @returns
 */
export function getFormBindFieldList(formId, page, rows) {
    const params = {
        formId,
        page,
        rows
    };
    return itemAdminRequest({
        url: '/vue/y9form/getFormBindFieldList',
        method: 'get',
        params: params
    });
}

/**
 * 删除绑定字段
 * @param id
 * @returns
 */
export function deleteFormFieldBind(id) {
    const params = {
        id
    };
    return itemAdminRequest({
        url: '/vue/y9form/deleteFormFieldBind',
        method: 'post',
        params: params
    });
}
