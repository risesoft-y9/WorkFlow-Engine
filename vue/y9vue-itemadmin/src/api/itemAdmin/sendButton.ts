/*
 * @Descripttion:
 * @version:
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: mengjuhua
 * @LastEditTime: 2026-01-08 14:09:18
 * @FilePath: \vue\y9vue-itemAdmin\src\api\itemAdmin\sendButton.ts
 */

import Request from '@/api/lib/request';
import qs from 'qs';

var itemAdminRequest = new Request();

//获取按钮列表
export function getSendButtonList() {
    const params = {};
    return itemAdminRequest({
        url: '/vue/sendButton/getSendButtonList',
        method: 'get',
        params: params
    });
}

//获取按钮信息
export function getSendButton(id) {
    const params = {
        id: id
    };
    return itemAdminRequest({
        url: '/vue/sendButton/getSendButton',
        method: 'get',
        params: params
    });
}

//保存按钮
export function saveSendButton(sendButton) {
    const data = qs.stringify(sendButton);
    return itemAdminRequest({
        url: '/vue/sendButton/saveOrUpdate',
        method: 'post',
        data: data
    });
}

//删除按钮
export function removeSendButton(sendButtonIds) {
    const params = {
        sendButtonIds: sendButtonIds
    };
    return itemAdminRequest({
        url: '/vue/sendButton/removeSendButtons',
        method: 'post',
        params: params
    });
}

//校验按钮是否存在
export function checkSendCustomId(customId) {
    const params = {
        customId: customId
    };
    return itemAdminRequest({
        url: '/vue/sendButton/checkCustomId',
        method: 'get',
        params: params
    });
}
