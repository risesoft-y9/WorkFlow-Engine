/*
 * @version:
 * @Author: zhangchongjie
 * @Date: 2024-05-11 16:39:47
 * @LastEditors: mengjuhua
 * @LastEditTime: 2026-06-29 14:48:27
 * @Descripttion:  快捷发送 
 * @FilePath: \y9-flowable\vue\y9vue-flowableUI\src\api\flowableUI\quickSend.ts
 */
import Request from '@/api/lib/request';

var flowableRequest = new Request();

/**
 * 获取快捷发送人
 * @param itemId
 * @param taskKey
 * @returns
 */
export function getAssignee(itemId, taskKey) {
    const params = {
        itemId,
        taskKey
    };
    return flowableRequest({
        url: '/vue/quickSend/getAssignee',
        method: 'get',
        params: params
    });
}

/**
 * 保存快捷发送人
 * @param itemId
 * @param taskKey
 * @param assignee
 * @returns
 */
export function saveOrUpdate(itemId, taskKey, assignee) {
    const params = {
        itemId,
        taskKey,
        assignee
    };
    return flowableRequest({
        url: '/vue/quickSend/saveOrUpdate',
        method: 'POST',
        params: params
    });
}
