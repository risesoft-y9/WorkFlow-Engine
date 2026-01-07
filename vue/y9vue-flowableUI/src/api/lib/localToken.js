/*
 * @Author: your name
 * @Date: 2021-10-08 11:04:48
 * @LastEditTime: 2026-01-07 10:19:34
 * @LastEditors: mengjuhua
 * @Description:
 * @FilePath: \vue\y9vue-flowableUI\src\api\lib\localToken.js
 */
import Cookies from 'js-cookie'; // 考虑CDN
import { siteTokenKey } from '@/settings';

/**
 * 获取本地Token
 * @author Y9
 */
export function getToken() {
    return Cookies.get(siteTokenKey);
}

/**
 * 设置存储Token
 * @author Y9
 */
export function setToken(token) {
    return Cookies.set(siteTokenKey, token);
}

/**
 * 移除本地Token
 * @author Y9
 */
export function removeToken() {
    return Cookies.remove(siteTokenKey);
}
