/*
 * @Author: your name
 * @Date: 2022-01-10 18:09:52
 * @LastEditTime: 2024-05-11 16:12:48
 * @LastEditors: zhangchongjie
 * @Description: 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-vue\y9vue-flowableUI\src\main.ts
 */
import router from "@/router/index"
import { setupStore } from '@/store'
import 'animate.css'
import 'normalize.css' // 样式初始化
import 'remixicon/fonts/remixicon.css'
import { createApp } from 'vue'
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
import y9_zhCn from "y9plugin-components/src/language/zh-cn";//默认的y9组件插件中文包
import y9_en from "y9plugin-components/src/language/en";//默认的y9组件插件英文包
import { useSettingStore } from "@/store/modules/settingStore";

import FormMakingV3 from './index';

import opinionList from "./views/opinion/opinionList.vue";
import formFileList from "./views/file/formFileList.vue";
import numberButton from "./views/number/numberButton.vue";
import personTree from "./views/personTree/index.vue";
import customPicture from "./views/customPicture/index.vue";
import y9pluginComponents from "y9plugin-components"

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
    // VUE_APP_REDISKEY: import.meta.env.VUE_APP_REDISKEY, //sso-redisKey
    // VUE_APP_SESSIONSTORAGE_GUID: import.meta.env.VUE_APP_SESSIONSTORAGE_GUID, //sso-sessionStorage_guid
    // VUE_APP_SERVER_REDIS: import.meta.env.VUE_APP_SERVER_REDIS //sso-redisServerUrl
  },
  logInfo: {
    showLog: true
  }
}



const app: any = createApp(App)
// app.use(ElementPlus, {locale: en})
app.use(sso, { env })

setupStore(app)
let opts = ref({}) //y9组件选项配置
watch(
  () => useSettingStore().getWebLanguage, //监听语言变化，配置对应的语言包
  (newLang) => {
    opts.value.locale = newLang === 'en' ? y9_en : y9_zhCn
  },
  {
    immediate: true
  }
)
app.use(i18n)
app.use(y9pluginComponents, opts.value)
app.use(router)
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

//流程设计
import MyPD from './components/bpmnModel/package/index.js';
app.use(MyPD);
import './components/bpmnModel/package/theme/index.scss';
//流程设计

app.mount('#app')

export const $y9_SSO = app.$y9_SSO;
