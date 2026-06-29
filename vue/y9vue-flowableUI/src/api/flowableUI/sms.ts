/*
 * @version:
 * @Author: zhangchongjie
 * @Date: 2024-05-11 16:39:47
 * @LastEditors: mengjuhua
 * @LastEditTime: 2026-06-29 14:48:57
 * @Descripttion:  发送短信相关  
 * @FilePath: \y9-flowable\vue\y9vue-flowableUI\src\api\flowableUI\sms.ts
 */
import Request from '@/api/lib/request';

import { SmsParam } from './dto';

var flowableRequest = new Request();

//保存短信提醒内容 
export function saveSms(param: SmsParam) {
    return flowableRequest({
        url: '/vue/smsDetail/saveOrUpdate',
        method: 'post',
        data: param,
        cType: true
    });
}
