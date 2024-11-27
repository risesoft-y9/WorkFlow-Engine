/*
 * @Descripttion:
 * @version:
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2021-06-22 16:39:50
 * @FilePath: \workspace-y9boot-9.5.x-vue\y9vue-itemAdmin\src\api\itemAdmin\item\printConfig.js
 */

import Request from '@/api/lib/request';

var itemAdminRequest = new Request();

//获取绑定打印模板列表
export function getBindTemplateList(itemId) {
    const params = {
        itemId: itemId
    };
    return itemAdminRequest({
        url: '/vue/printTemplate/getBindTemplateList',
        method: 'get',
        params: params
    });
}

//删除打印模板
export function deleteBindPrintTemplate(id) {
    const params = {
        id: id
    };
    return itemAdminRequest({
        url: '/vue/printTemplate/deleteBindPrintTemplate',
        method: 'post',
        params: params
    });
}

//保存绑定打印模板
export function saveBindTemplate(itemId, templateId, templateName, templateType, templateUrl) {
    const params = {
        itemId: itemId,
        templateId: templateId,
        templateName: templateName,
        templateType: templateType,
        templateUrl: templateUrl
    };
    return itemAdminRequest({
        url: '/vue/printTemplate/saveBindTemplate',
        method: 'post',
        params: params
    });
}

//获取打印表单列表
export function getPrintFormList(itemId, formName) {
    const params = {
        itemId: itemId,
        formName: formName
    };
    return itemAdminRequest({
        url: '/vue/y9form/item/getPrintFormList',
        method: 'get',
        params: params
    });
}
