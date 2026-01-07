<!--
 * @Author: mengjuhua
 * @Date: 2026-01-05 16:44:53
 * @LastEditors: mengjuhua
 * @LastEditTime: 2026-01-07 10:53:42
 * @FilePath: \vue\y9vue-flowableUI\src\views\customPicture\index.vue
 * @Description: 自定义图片显示组件
-->
<template>
    <img :src="imgsrc" style="width: 150px; height: 150px" @click="imgClick" />
</template>

<script lang="ts" setup>
    import { inject, reactive, toRefs } from 'vue';
    import { useI18n } from 'vue-i18n';

    const { t } = useI18n();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo') || {};
    const props = defineProps({
        tableField: String //关联表单字段
    });

    const data = reactive({
        basicData: {},
        imgsrc: ''
    });

    let { basicData, imgsrc } = toRefs(data);

    defineExpose({
        initPicture
    });

    function initPicture(data, formData) {
        console.log('加载图片');
        if (formData != undefined) {
            imgsrc.value = formData[props.tableField];
        }
        basicData.value = data;
    }

    function imgClick() {
        console.log('图片点击事件');
    }
</script>

<style lang="scss" scoped></style>

<style lang="scss"></style>
