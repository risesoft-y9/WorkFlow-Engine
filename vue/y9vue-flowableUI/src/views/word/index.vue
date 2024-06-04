<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2023-11-28 11:37:43
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2024-04-01 11:23:06
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-vue\y9vue-flowableUI\src\views\word\index.vue
-->
<template>
  <img src="@/assets/word.png" style="width: 35px;vertical-align: middle;display: inline-block;" @click="openWord" alt="">
  <span id="risesoftNTKOWord" style="color:red;display: none;vertical-align: middle;font-size: 16px;">不能装载文档控件,请在检查浏览器的选项中检查浏览器的安全设置,或者下载安装<a title="点击下载" :href="href" style="color: blue;cursor:pointer;text-decoration: none;">跨浏览器插件</a>
  </span>  
</template>

<script lang="ts" setup>
import { ntkoBrowser } from '@/assets/js/ntkobackground.min.js';
import {inject} from 'vue';
import { useI18n } from 'vue-i18n';
import y9_storage from '@/utils/storage';
const { t } = useI18n();
// 注入 字体对象
const fontSizeObj: any = inject('sizeObjInfo')||{}; 

const data = reactive({
  basicData:{},
  wordUrl: import.meta.env.VUE_APP_CONTEXT + 'webOfficeNTKO.html',
  href: import.meta.env.VUE_APP_CONTEXT + 'tags/exe/risesoftNTKOWord.exe',
});

let {
  basicData,
  wordUrl,
  href
} = toRefs(data);

 defineExpose({
  initWord
 });

  function initWord(data){
    console.log("加载正文组件...");
    basicData.value = data;
  }

  function openWord() {
    let y9UserInfo = y9_storage.getObjectItem('ssoUserInfo');
    //打开word
    let userAgent = navigator.userAgent;
    let rMsie = /(msie\s|trident.*rv:)([\w.]+)/;
    let rFirefox = /(firefox)\/([\w.]+)/;
    let rOpera = /(opera).+versi1on\/([\w.]+)/;
    let rChrome = /(chrome)\/([\w.]+)/;
    let rSafari = /version\/([\w.]+).*(safari)/;
    let browser;
    let ua = userAgent.toLowerCase();
    let match = rMsie.exec(ua);
    if (match != null) {
        browser = 'IE';
    }
    match = rFirefox.exec(ua);
    if (match != null) {
        browser = match[1] || '';
    }
    match = rOpera.exec(ua);
    if (match != null) {
        browser = match[1] || '';
    }
    match = rChrome.exec(ua);
    if (match != null) {
        browser = match[1] || '';
    }
    match = rSafari.exec(ua);
    if (match != null) {
        browser = match[2] || '';
    }
    if (match != null) {
        browser = '';
    }
    let positionId = sessionStorage.getItem('positionId');
    let msg = {
      msgType: 'openWord',
      itemId: basicData.value.itemId,
      itembox: basicData.value.itembox,
      processSerialNumber: basicData.value.processSerialNumber,
      processInstanceId: basicData.value.processInstanceId,
      taskId: basicData.value.taskId,
      browser: browser,
      tenantId: y9UserInfo.tenantId,
      userId: y9UserInfo.personId,
      positionId: positionId
    };
    // wordIframe.value?.contentWindow?.postMessage(msg, wordUrl.value);
    if(!ntkoBrowser.ExtensionInstalled()){
      document.getElementById('risesoftNTKOWord').style.display = "";
    }else{
      ntkoBrowser.openWindow(wordUrl.value + "?cmd=1&apiCtx="+import.meta.env.VUE_APP_CONTEXT+"&itembox="+basicData.value.itembox+"&processSerialNumber="+basicData.value.processSerialNumber+"&itemId="
			  +basicData.value.itemId+"&taskId="+basicData.value.taskId+"&processInstanceId="+basicData.value.processInstanceId+"&browser="+browser+"&tenantId="+y9UserInfo.tenantId+"&userId="+y9UserInfo.personId+"&positionId="+positionId, false);
    }
  }

  
</script>

<style scoped lang="scss">
</style>

<style lang="scss">
  :deep(.wordIframe) {
    #wordformIframe{
      // display: none !important;
    }
  }
</style>