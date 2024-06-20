<!--
 * @Author: your name
 * @Date: 2022-01-10 18:09:52
 * @LastEditTime: 2022-01-11 15:41:06
 * @LastEditors: Please set LastEditors
 * @Description: 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 * @FilePath: /sz- team-frontend-9.6.x/y9vue-flowableUI/src/App.vue
-->
<script lang="ts" setup>

	import { ElConfigProvider } from 'element-plus'
	import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
	import en from 'element-plus/dist/locale/en.mjs';
	import watermark from'y9plugin-watermark/lib/index'
	import y9_storage from '@/utils/storage';
	// 引入字体调整的方法
	import { getConcreteSize } from '@/utils/index';
	import { onUnmounted ,onBeforeMount,onMounted,watch, computed, ref, provide} from 'vue';
	import { useSettingStore } from "@/store/modules/settingStore";
	import { useI18n } from 'vue-i18n';
	const { t } = useI18n();
	const settingStore = useSettingStore();
	let locale = computed(() => (settingStore.getWebLanguage === 'zh' ? zhCn : en));

    watch(
        () => useSettingStore().getWebLanguage, //监听语言变化，配置对应的语言包
        (newLang) => {
            locale = computed(() => (newLang === 'en' ? en : zhCn));
        },
        {
            immediate: true
        }
    );

	onBeforeMount(()=>{
		if(window.location.href.indexOf("/workIndex/") > -1 && window.location.href.indexOf("/search/work") == -1 && window.location.href.indexOf("/readIndex") == -1){
			window.location.href = import.meta.env.VUE_APP_HOST_INDEX + "workIndex";
			return;
		}
	})



	interface watermarkData {
        text?,
        deptName?,
        name?
    }
    // 定义⽔印⽂字变量
    const userInfo = y9_storage.getObjectItem('ssoUserInfo');
    let dept = userInfo.dn?.split(',')[1]?.split('=')[1];
    let watermarkValue = ref<watermarkData>({
        name: userInfo.name,
		text: computed(() => t('保守秘密，慎之又慎')),
		deptName: dept,
    });
	//监听语言变化，传入对应的水印语句
	watch(
		() =>useSettingStore().getWebLanguage,
		(newLang) => {
			setTimeout(() =>{
				watermarkValue.value.name = t(userInfo.name);
				watermarkValue.value.deptName = t(dept);	
				watermark(watermarkValue, sizeObjInfo.value.baseFontSize);
			})  
		},
	)
	watch( 
	() => useSettingStore().getFontSize, //监听大小变化，传入对应水印文字大小
		 (newLang) => { 
			setTimeout(() =>{
				watermarkValue.value.name = t(userInfo.name);
				watermarkValue.value.deptName = t(dept);	
				watermark(watermarkValue, sizeObjInfo.value.baseFontSize);
			})  
		}
	)
	onMounted(() => {
		// 执⾏⽔印⽅法
        setTimeout(() => {
            watermarkValue.value.name = t(userInfo.name);
            watermarkValue.value.deptName = t(dept);	
            watermark(watermarkValue, sizeObjInfo.value.baseFontSize);
        })
	});
	onUnmounted(() =>{
		watermark('');
	});

	// 主题切换
    const theme = computed(() => settingStore.getThemeName)
    const toggleColor = (theme) => {
        if (document.getElementById("head")) {
            let themeDom = document.getElementById("head")
            let pathArray = themeDom.href.split('/')
            pathArray[pathArray.length-1] = theme + '.css'
            let newPath = pathArray.join('/')
            themeDom.href = newPath
        }
    }
    toggleColor(theme.value);

	/***
	 *  字体大中小
	 * 定义变量
	 */

	let sizeObjInfo = ref({
		smallFontSize: getConcreteSize(settingStore.getFontSize, 12) + 'px',
		baseFontSize: getConcreteSize(settingStore.getFontSize, 14) + 'px',
		mediumFontSize: getConcreteSize(settingStore.getFontSize, 16) + 'px',
		largeFontSize: getConcreteSize(settingStore.getFontSize, 18) + 'px',
		largerFontSize: getConcreteSize(settingStore.getFontSize, 19) + 'px',
		extraLargeFont: getConcreteSize(settingStore.getFontSize, 20) + 'px', 
		extrarLargeFont: getConcreteSize(settingStore.getFontSize, 22) + 'px',
		extraLargerFont: getConcreteSize(settingStore.getFontSize, 24) + 'px',
		moreLargeFont: getConcreteSize(settingStore.getFontSize, 26) + 'px',
		morerLargeFont: getConcreteSize(settingStore.getFontSize, 30) + 'px',
		moreLargerFont: getConcreteSize(settingStore.getFontSize, 32) + 'px',
		biggerrFontSize: getConcreteSize(settingStore.getFontSize, 38) + 'px',
		biggerFontSize: getConcreteSize(settingStore.getFontSize, 40) + 'px',
		maximumFontSize: getConcreteSize(settingStore.getFontSize, 48) + 'px',
		buttonSize: settingStore.getFontSize,
		lineHeight: settingStore.getLineHeight

	})
	// 监听 转换font-size值
	watch(() => settingStore.getFontSize, (newVal) => {
		sizeObjInfo.value.smallFontSize = getConcreteSize(newVal, 12) + 'px';
		sizeObjInfo.value.baseFontSize = getConcreteSize(newVal, 14) + 'px';
		sizeObjInfo.value.mediumFontSize = getConcreteSize(newVal, 16) + 'px';
		sizeObjInfo.value.largeFontSize = getConcreteSize(newVal, 18) + 'px'; 
		sizeObjInfo.value.largerFontSize = getConcreteSize(newVal, 19) + 'px';
		sizeObjInfo.value.extraLargeFont = getConcreteSize(newVal, 20) + 'px';
		sizeObjInfo.value.extrarLargeFont = getConcreteSize(newVal, 22) + 'px';
		sizeObjInfo.value.extraLargerFont = getConcreteSize(newVal, 24) + 'px';
		sizeObjInfo.value.moreLargeFont = getConcreteSize(newVal, 26) + 'px';
		sizeObjInfo.value.morerLargeFont = getConcreteSize(newVal, 30) + 'px';
		sizeObjInfo.value.moreLargerFont = getConcreteSize(newVal, 32) + 'px';
		sizeObjInfo.value.biggerrFontSize = getConcreteSize(newVal, 38) + 'px';
		sizeObjInfo.value.biggerFontSize = getConcreteSize(newVal, 40) + 'px';
		sizeObjInfo.value.maximumFontSize = getConcreteSize(newVal, 48) + 'px';
		sizeObjInfo.value.buttonSize = newVal;
		sizeObjInfo.value.lineHeight = settingStore.getLineHeight;

	})

	// provide提供
	provide('sizeObjInfo', sizeObjInfo.value);
</script>

<template>
	<el-config-provider :locale="locale.value" >
		<router-view></router-view>
    </el-config-provider>
</template>


