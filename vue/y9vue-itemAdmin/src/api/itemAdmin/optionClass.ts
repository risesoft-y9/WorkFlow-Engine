/*
 * @Descripttion:
 * @version:
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2021-08-12 10:21:55
 * @FilePath: \workspace-y9boot-9.5.x-vue\y9vue-itemAdmin\src\api\itemAdmin\optionClass.js
 */

import Request from '@/api/lib/request';
import qs from 'qs';

var itemAdminRequest = new Request();

//获取数据字典列表
export function getOptionClassList() {
    const params = {
        name: ''
    };
    return itemAdminRequest({
        url: '/vue/y9form/optionClass/getOptionClassList',
        method: 'get',
        params: params
    });
}

//获取数据字典
export function getOptionClass(type) {
    const params = {
        type: type
    };
    return itemAdminRequest({
        url: '/vue/y9form/optionClass/getOptionClass',
        method: 'get',
        params: params
    });
}

//保存数据字典
export function saveOptionClass(optionClass) {
    const data = qs.stringify(optionClass);
    return itemAdminRequest({
        url: '/vue/y9form/optionClass/saveOptionClass',
        method: 'post',
        data: data
    });
}

//删除数据字典
export function delOptionClass(type) {
    const params = {
        type: type
    };
    return itemAdminRequest({
        url: '/vue/y9form/optionClass/delOptionClass',
        method: 'post',
        params: params
    });
}

//获取数据字典值列表
export function getOptionValueList(type) {
    const params = {
        type: type
    };
    return itemAdminRequest({
        url: '/vue/y9form/optionClass/getOptionValueList',
        method: 'get',
        params: params
    });
}

//获取数据字典值
export function getOptionValue(id) {
    const params = {
        id: id
    };
    return itemAdminRequest({
        url: '/vue/y9form/optionClass/getOptionValue',
        method: 'get',
        params: params
    });
}

//保存数据字典值
export function saveOptionValue(optionValue) {
    const data = qs.stringify(optionValue);
    return itemAdminRequest({
        url: '/vue/y9form/optionClass/saveOptionValue',
        method: 'post',
        data: data
    });
}

//删除数据字典值
export function delOptionValue(id) {
    const params = {
        id: id
    };
    return itemAdminRequest({
        url: '/vue/y9form/optionClass/delOptionValue',
        method: 'post',
        params: params
    });
}

//设置默认选中
export function updateOptionValue(id) {
    const params = {
        id: id
    };
    return itemAdminRequest({
        url: '/vue/y9form/optionClass/updateOptionValue',
        method: 'post',
        params: params
    });
}

//保存排序
export function saveOrder(ids) {
    const params = {
        ids: ids
    };
    return itemAdminRequest({
        url: '/vue/y9form/optionClass/saveOrder',
        method: 'post',
        params: params
    });
}
