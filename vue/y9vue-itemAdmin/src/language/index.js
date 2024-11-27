/*
 * @Author: your name
 * @Date: 2022-02-18 14:49:54
 * @LastEditTime: 2023-07-10 16:58:44
 * @LastEditors: mengjuhua
 * @Description: 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 * @FilePath: /Vue3x/work/demoVue3/src/language/index.js
 */

import zh from './zh.json';
import en from './en.json';
import {createI18n} from 'vue-i18n';
// 获取本地语言
const info = JSON.parse(localStorage.getItem('userSettingData'));
const messages = {
    zh: {
        ...zh
    },
    en: {
        ...en
    }
};

// 用于动态功能配置
const i18n = createI18n({
    locale: info?.webLanguage || 'zh',
    // 使用 Composition API 模式，则需要将其设置为false
    legacy: false,
    // 全局注入 $t 函数
    globalInjection: true,
    messages
});

export default i18n;
