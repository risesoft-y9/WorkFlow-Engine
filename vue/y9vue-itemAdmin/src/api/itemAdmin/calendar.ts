/*
 * @Descripttion:
 * @version:
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2021-06-18 17:06:23
 * @FilePath: \workspace-y9boot-9.5.x-vue\y9vue-itemAdmin\src\api\itemAdmin\calendar.js
 */

import Request from '@/api/lib/request';

var itemAdminRequest = new Request();

//获取日期配置
export function getCalendar(month) {
    const params = {
        month: month
    };
    return itemAdminRequest({
        url: '/vue/calendar/getCalendar',
        method: 'get',
        params: params
    });
}

//保存日期配置
export function saveCalendar(startDate, type) {
    const params = {
        startDate: startDate,
        type: type
    };
    return itemAdminRequest({
        url: '/vue/calendar/saveCalendar',
        method: 'post',
        params: params
    });
}

//删除日期配置
export function delCalendar(startDate) {
    const params = {
        startDate: startDate
    };
    return itemAdminRequest({
        url: '/vue/calendar/delCalendar',
        method: 'post',
        params: params
    });
}
