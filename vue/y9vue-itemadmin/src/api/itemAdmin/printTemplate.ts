/*
 * @Descripttion:
 * @version:
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: mengjuhua
 * @LastEditTime: 2026-01-08 14:09:01
 * @FilePath: \vue\y9vue-itemAdmin\src\api\itemAdmin\printTemplate.ts
 */

import Request from '@/api/lib/request';

var itemAdminRequest = new Request();

//获取打印模板列表
export function getPrintTemplateList(fileName) {
    const params = {
        fileName: fileName
    };
    return itemAdminRequest({
        url: '/vue/printTemplate/getPrintTemplateList',
        method: 'get',
        params: params
    });
}

//上传打印模板
export function uploadTemplate(file) {
    let formData = new FormData();
    formData.append('files', file);
    return itemAdminRequest({
        url: '/vue/printTemplate/uploadTemplate',
        method: 'post',
        data: formData
    });
}

//删除打印模板
export function deletePrintTemplate(id) {
    const params = {
        id: id
    };
    return itemAdminRequest({
        url: '/vue/printTemplate/deletePrintTemplate',
        method: 'post',
        params: params
    });
}

//下载打印模板
export function download(id) {
    const params = { id: id };
    return itemAdminRequest({
        url: '/vue/printTemplate/download',
        method: 'get',
        params: params
    });
}
