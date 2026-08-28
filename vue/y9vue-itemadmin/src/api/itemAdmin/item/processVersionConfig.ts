/*
 * @Descripttion:
 * @version:
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: mengjuhua
 * @LastEditTime: 2026-01-08 14:07:13
 * @FilePath: \vue\y9vue-itemAdmin\src\api\itemAdmin\item\processVersionConfig.ts
 */

import Request from '@/api/lib/request';

var itemAdminRequest = new Request();

//复制事项和流程定义版本相关的绑定
export function copyAllBind(itemId, processDefinitionId) {
    const params = {
        itemId: itemId,
        processDefinitionId: processDefinitionId
    };
    return itemAdminRequest({
        url: '/vue/item/copyAllBind',
        method: 'post',
        params: params
    });
}
