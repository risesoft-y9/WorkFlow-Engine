<template>
    <img :src="imgsrc" style="width: 150px; height: 150px" @click="imgClick" />
</template>

<script lang="ts" setup>
    import { inject } from 'vue';
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
