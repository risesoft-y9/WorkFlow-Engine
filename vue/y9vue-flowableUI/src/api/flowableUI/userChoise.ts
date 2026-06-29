/* 
 * @version:
 * @Author: zhangchongjie
 * @Date: 2021-05-27 16:33:53
 * @LastEditors: mengjuhua
 * @LastEditTime: 2026-06-29 14:49:19
 * @Descripttion: 发送相关接口 
 * @FilePath: \y9-flowable\vue\y9vue-flowableUI\src\api\flowableUI\userChoise.ts
 */
import Request from '@/api/lib/request';

var flowableRequest = new Request();

//获取发送选人数据
export function getUserChoiseData(itemId, routeToTask, processDefinitionId, taskId, processInstanceId) {
    const params = {
        itemId: itemId,
        routeToTask: routeToTask,
        processDefinitionId: processDefinitionId,
        taskId: taskId,
        processInstanceId: processInstanceId
    };
    return flowableRequest({
        url: '/vue/userChoise/userChoiseData',
        method: 'get',
        params: params
    });
}

//获取发送选人人数
export function getUserCount(userChoice) {
    const params = {
        userChoice: userChoice
    };
    return flowableRequest({
        url: '/vue/rolePerson/getUserCount',
        method: 'get',
        params: params
    });
}
