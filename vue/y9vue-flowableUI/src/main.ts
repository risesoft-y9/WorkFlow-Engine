/*
 * @Author: your name
 * @Date: 2022-01-10 18:09:52
 * @LastEditTime: 2026-01-06 14:19:00
 * @LastEditors: mengjuhua
 * @Description: 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 * @FilePath: \vue\y9vue-flowableUI\src\main.ts
 */
import router from "@/router/index"
import { setupStore } from '@/store'
import 'animate.css'
import 'normalize.css' // 样式初始化
import 'remixicon/fonts/remixicon.css'
import { createApp, ref, watch } from 'vue'
import sso from "y9plugin-sso"
import App from './App.vue'
import './theme/global.scss'
import i18n from "./language"

//打印文件
import print from 'vue3-print-nb'


import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import en from 'element-plus/dist/locale/zh-cn.mjs'
import zh from 'element-plus/dist/locale/en.mjs'

import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css';
import { jsPDF } from 'jspdf'
import html2canvas from 'html2canvas';



import FormMakingV3 from './index';

import opinionList from "./views/opinion/opinionList.vue";
import formFileList from "./views/file/formFileList.vue";
import numberButton from "./views/number/numberButton.vue";
import personTree from "./views/personTree/index.vue";
import customPicture from "./views/customPicture/index.vue";


//有生云公共组件库
import y9pluginComponents from 'y9plugin-components-auto';
import 'y9plugin-components-auto/dist/style.css';
import y9_zhCn from 'y9plugin-components-auto/dist/locale/zh-cn.mjs'; //默认的y9组件插件中文包
import y9_en from 'y9plugin-components-auto/dist/locale/en.mjs'; //默认的y9组件插件英文包
import { useSettingStore } from '@/store/modules/settingStore';
import customDirective from '@/utils/directive'; //自定义指令

// 传入sso所需的环境变量
const env = {
    sso: {
        VUE_APP_SSO_DOMAINURL: import.meta.env.VUE_APP_SSO_DOMAINURL, // sso接口
        VUE_APP_SSO_CONTEXT: import.meta.env.VUE_APP_SSO_CONTEXT, // sso接口上下文
        VUE_APP_SSO_AUTHORIZE_URL: import.meta.env.VUE_APP_SSO_AUTHORIZE_URL, //sso授权码接口
        VUE_APP_SSO_LOGOUT_URL: import.meta.env.VUE_APP_SSO_LOGOUT_URL, //退出URL
        VUE_APP_SSO_CLIENT_ID: import.meta.env.VUE_APP_SSO_CLIENT_ID, //sso接口的固定字段
        VUE_APP_SSO_SECRET: import.meta.env.VUE_APP_SSO_SECRET, //sso接口的固定字段
        VUE_APP_SSO_GRANT_TYPE: import.meta.env.VUE_APP_SSO_GRANT_TYPE, //sso接口的固定字段
        VUE_APP_SSO_SITETOKEN_KEY: import.meta.env.VUE_APP_SSO_SITETOKEN_KEY, //sso-token_key
        VUE_APP_HOST_LICENSE: import.meta.env.VUE_APP_HOST_LICENSE,
        VUE_APP_CONTEXT: import.meta.env.VUE_APP_CONTEXT
    },
    logInfo: {
        showLog: true
    }
}

const app: any = createApp(App)
// app.use(ElementPlus, {locale: en})
app.use(sso, { env })

setupStore(app)
let opts = ref({} as any) //y9组件选项配置
watch(
    () => useSettingStore().getWebLanguage, //监听语言变化，配置对应的语言包
    (newLang) => {
        opts.value.locale = newLang === 'en' ? y9_en : y9_zhCn
    },
    {
        immediate: true
    }
)

app.use(y9pluginComponents, opts.value)
app.use(router);
app.use(customDirective);

// app.use(y9pluginComponents)
// 获取本地语言
const info = JSON.parse(localStorage.getItem('userSettingData'));
app.use(FormMakingV3, {
    locale: info?.webLanguage || 'zh',
    jsPDF,
    html2canvas,
    components: [
        {//加入意见框组件
            name: 'custom-opinion',
            component: opinionList
        },
        {//加入附件列表组件
            name: 'custom-file',
            component: formFileList
        },
        {//加入人员树组件
            name: 'custom-personTree',
            component: personTree
        },
        {//加入编号按钮组件
            name: 'custom-numberButton',
            component: numberButton
        },
        {//加入图片显示组件
            name: 'custom-picture',
            component: customPicture
        }
    ]
})
app.component('QuillEditor', QuillEditor)
app.use(print)
// app.use(i18n)

app.use(i18n);
app.mount('#app')

export const $y9_SSO = app.$y9_SSO;
