/*
 * @Author: your name
 * @Date: 2021-05-19 09:41:06
 * @LastEditTime: 2026-06-29 14:48:05
 * @LastEditors: mengjuhua
 * @Description: 历程信息 
 * @FilePath: \y9-flowable\vue\y9vue-flowableUI\src\api\flowableUI\process.ts
 */
import Request from '@/api/lib/request';

var flowableRequest = new Request();

//获取简易历程
export function processList(processInstanceId) {
    const params = {
        processInstanceId: processInstanceId
    };
    return flowableRequest({
        url: '/vue/processTrack/processList',
        method: 'get',
        params: params
    });
}

//获取历程信息
export function historyList(processInstanceId) {
    const params = {
        processInstanceId: processInstanceId
    };
    return flowableRequest({
        url: '/vue/processTrack/historyList',
        method: 'get',
        params: params
    });
}
