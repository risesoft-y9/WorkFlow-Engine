/*
 * @Author: your name
 * @Date: 2021-05-19 09:41:06
 * @LastEditTime: 2026-01-07 10:31:47
 * @LastEditors: mengjuhua
 * @Description: In User Settings Edit
 * @FilePath: \vue\y9vue-flowableUI\src\api\flowableUI\processParam.ts
 */
import Request from '@/api/lib/request';

var flowableRequest = new Request();
//保存自定义变量数据
export function saveProcessParam(itemId, processSerialNumber, processInstanceId, documentTitle, number, level, customItem) {
    const params = {
        processSerialNumber: processSerialNumber,
        itemId: itemId,
        processInstanceId: processInstanceId,
        documentTitle: documentTitle,
        number: number,
        level: level,
        customItem: customItem
    };
    return flowableRequest({
        url: '/vue/processParam/saveOrUpdate',
        method: 'POST',
        params: params
    });
}
