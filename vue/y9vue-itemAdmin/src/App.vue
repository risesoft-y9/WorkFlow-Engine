<!--
 * @Author: your name
 * @Date: 2022-01-10 18:09:52
 * @LastEditTime: 2023-06-15 16:52:54
 * @LastEditors: zhangchongjie
 * @Description: 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9boot-9.6-vue\y9vue-itemAdmin\src\App.vue
-->
<script lang="ts" setup>
    import watermark from 'y9plugin-watermark/lib/index';
    import { ElConfigProvider } from 'element-plus';
    import { computed, onMounted, onUnmounted, ref, watch } from 'vue';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useI18n } from 'vue-i18n';
    import zhCn from 'element-plus/dist/locale/zh-cn.mjs';
    import y9_storage from '@/utils/storage';

    const settingStore = useSettingStore();

    const { t } = useI18n();

    const locale = zhCn;

    interface watermarkData {
        text?;
        deptName?;
        name?;
    }

    // 定义⽔印⽂字变量
    const userInfo = y9_storage.getObjectItem('ssoUserInfo');
    let dept = userInfo.dn?.split(',')[1]?.split('=')[1];
    let watermarkValue = ref<watermarkData>({
        name: userInfo.name,
        text: computed(() => t('保守秘密，慎之又慎')),
        deptName: dept
    });
    //监听语言变化，传入对应的水印语句
    watch(
        () => useSettingStore().getWebLanguage,
        (newLang) => {
            setTimeout(() => {
                watermarkValue.value.name = t(userInfo.name);
                watermarkValue.value.deptName = t(dept);
                watermark(watermarkValue);
            });
        }
    );
    onMounted(() => {
        // 执⾏⽔印⽅法
        setTimeout(() => {
            watermarkValue.value.name = t(userInfo.name);
            watermarkValue.value.deptName = t(dept);
            watermark(watermarkValue);
        });
    });
    onUnmounted(() => {
        watermark('');
    });

    // 主题切换
    const theme = computed(() => settingStore.getThemeName);
    const toggleColor = (theme) => {
        if (document.getElementById('head')) {
            let themeDom = document.getElementById('head');
            let pathArray = themeDom.href.split('/');
            pathArray[pathArray.length - 1] = theme + '.css';
            let newPath = pathArray.join('/');
            themeDom.href = newPath;
        }
    };
    toggleColor(theme.value);
</script>

<template>
    <el-config-provider :locale="locale">
        <router-view></router-view>
    </el-config-provider>
</template>
