/*
 * @version:
 * @Author: zhangchongjie
 * @Date: 2024-05-11 16:39:47
 * @LastEditors: mengjuhua
 * @LastEditTime: 2026-06-29 14:46:29
 * @Descripttion:  请假统计列表
 * @FilePath: \y9-flowable\vue\y9vue-flowableUI\src\api\flowableUI\leaveCount.ts
 */
import Request from '@/api/lib/request';
var flowableRequest = new Request();

//获取请假统计列表
export function countList(leaveType, userName, deptName, startTime, endTime) {
    const params = {
        leaveType: leaveType,
        userName: userName,
        deptName: deptName,
        startTime: startTime,
        endTime: endTime
    };
    return flowableRequest({
        url: '/vue/leaveCount/countList',
        method: 'get',
        params: params
    });
}
