/*
 * @Author: your name
 * @Date: 2021-05-19 09:41:06
 * @LastEditTime: 2026-03-03 13:47:50
 * @LastEditors: zhangchongjie
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
    let formData = new FormData();
    formData.append('processSerialNumber', processSerialNumber);
    formData.append('itemId', itemId);
    formData.append('processInstanceId', processInstanceId);
    formData.append('documentTitle', documentTitle);
    formData.append('number', number);
    formData.append('level', level);
    formData.append('customItem', customItem);
    return flowableRequest({
        url: '/vue/processParam/saveOrUpdate',
        method: 'POST',
        data: formData
    });
}
