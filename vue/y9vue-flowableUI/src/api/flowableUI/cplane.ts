/*
 * @version:
 * @Author: zhangchongjie
 * @Date: 2021-05-27 16:33:29
 * @LastEditors: mengjuhua
 * @LastEditTime: 2026-06-29 14:45:07
 * @Descripttion: 状态列表 
 * @FilePath: \y9-flowable\vue\y9vue-flowableUI\src\api\flowableUI\cplane.ts
 */

import Request from '@/api/lib/request';
var flowableRequest = new Request();

//获取协作状态列表
export function processInstanceList(page, rows, title) {
    const params = {
        page: page,
        rows: rows,
        title: title
    };
    return flowableRequest({
        url: '/vue/processInstance/processInstanceList',
        method: 'get',
        params: params
    });
}
