/*
 * @Author: your name
 * @Date: 2022-01-10 18:09:52
 * @LastEditTime: 2026-01-08 10:58:12
 * @LastEditors: mengjuhua
 * @Description: 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 * @FilePath: \vue\y9vue-itemAdmin\src\main.ts
 */
import router from '@/router/index';
import { setupStore } from '@/store';
import 'animate.css';
import 'normalize.css'; // 样式初始化
import 'remixicon/fonts/remixicon.css';
import { createApp, ref, watch } from 'vue';
import sso from 'y9plugin-sso';
import App from './App.vue';
import './theme/global.scss';
import numberButton from './components/formMaking/components/SecondDev/numberButton.vue';

import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import i18n from './language';

// 暗黑模式
// import 'element-plus/theme-chalk/dark/css-vars.css'
import en from 'element-plus/es/locale/lang/en';

import { jsPDF } from 'jspdf';
import html2canvas from 'html2canvas';
//富文本编辑器
import { QuillEditor } from '@vueup/vue-quill';
import '@vueup/vue-quill/dist/vue-quill.snow.css';
import FormMakingV3 from './index';

//有生云公共组件库
import y9pluginComponents from 'y9plugin-components-auto';
import 'y9plugin-components-auto/dist/style.css';
import y9_zhCn from 'y9plugin-components-auto/dist/locale/zh-cn.mjs'; //默认的y9组件插件中文包
import y9_en from 'y9plugin-components-auto/dist/locale/en.mjs'; //默认的y9组件插件英文包
import { useSettingStore } from '@/store/modules/settingStore';
import customDirective from '@/utils/directive'; //自定义指令

import 'viewerjs/dist/viewer.css';
import VueViewer from 'v-viewer';
import '@kangc/v-md-editor/lib/style/base-editor.css';
import '@kangc/v-md-editor/lib/theme/style/vuepress.css';
// 这是预览时引用的
import VMdPreview from '@kangc/v-md-editor/lib/preview';
import '@kangc/v-md-editor/lib/style/preview.css';
import highlight from 'highlight.js';
import githubTheme from '@kangc/v-md-editor/lib/theme/github';
import '@kangc/v-md-editor/lib/theme/style/github.css';
//流程设计
import MyPD from './components/bpmnModel/package/index.js';
import './components/bpmnModel/package/theme/index.scss';

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
        VUE_APP_SSO_SITETOKEN_KEY: import.meta.env.VUE_APP_SSO_SITETOKEN_KEY //sso-token_key
        // VUE_APP_REDISKEY: import.meta.env.VUE_APP_REDISKEY, //sso-redisKey
        // VUE_APP_SESSIONSTORAGE_GUID: import.meta.env.VUE_APP_SESSIONSTORAGE_GUID, //sso-sessionStorage_guid
        // VUE_APP_SERVER_REDIS: import.meta.env.VUE_APP_SERVER_REDIS //sso-redisServerUrl
    },
    logInfo: {
        showLog: true
    }
};

const app: any = createApp(App);
app.use(ElementPlus, { locale: en });
app.use(sso, { env });

setupStore(app);

let opts = ref({} as any); //y9组件选项配置
watch(
    () => useSettingStore().getWebLanguage, //监听语言变化，配置对应的语言包
    (newLang) => {
        opts.value.locale = newLang === 'en' ? y9_en : y9_zhCn;
    },
    {
        immediate: true
    }
);

app.use(y9pluginComponents, opts.value);
app.use(router);
app.use(customDirective);


app.use(VueViewer);
//预览的主题
VMdPreview.use(githubTheme, {
    Hljs: highlight
});
app.use(VMdPreview);
app.use(highlight);

//表单设计
app.use(FormMakingV3, {
    locale: 'zh-cn',
    jsPDF,
    html2canvas
});
app.component('QuillEditor', QuillEditor);
app.component('custom-numberButton', numberButton);
//表单设计

app.use(MyPD);
//流程设计
app.use(i18n);
app.mount('#app');

export const $y9_SSO = app.$y9_SSO;
